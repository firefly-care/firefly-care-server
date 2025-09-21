package org.farm.fireflyserver.domain.led.service.calculator;

import lombok.extern.slf4j.Slf4j;
import org.farm.fireflyserver.domain.led.persistence.AnomalyType;
import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;
import org.farm.fireflyserver.domain.led.persistence.entity.OnOff;

import org.farm.fireflyserver.domain.led.persistence.entity.SensorGbn;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CognitiveScoreCalculator implements ScoreCalculator {

    @Override
    public AnomalyType getAnomalyType() {
        return AnomalyType.COGNITIVE;
    }

    @Override
    public Double calculate(List<LedHistory> allEvents) {
        log.info("[CognitiveScoreCalculator] 계산 시작. 입력된 이벤트 수: {}", allEvents != null ? allEvents.size() : 0);
        if (allEvents == null || allEvents.isEmpty()) {
            log.warn("[CognitiveScoreCalculator] 입력된 이벤트가 없어 0.0을 반환합니다.");
            return 0.0;
        }

        // 각 기준별 발생 횟수 계산
        int c1Count = calculatePingPongMovement(allEvents);
        int c2Count = calculateRepetitiveReentry(allEvents);
        int c3Count = calculateForgettingToTurnOffLights(allEvents);
        int c4Count = calculateNightWandering(allEvents);
        log.info("[CognitiveScoreCalculator] 중간 계산 완료: [c1Count={}, c2Count={}, c3Count={}, c4Count={}]", c1Count, c2Count, c3Count, c4Count);

        // 계산된 횟수를 위험 지수(rc)로 변환
        Map<String, Double> riskScores = convertCountsToRiskScores(c1Count, c2Count, c3Count, c4Count);

        // 각 기준별 위험 지수
        double rc1 = riskScores.get("rc1");
        double rc2 = riskScores.get("rc2");
        double rc3 = riskScores.get("rc3");
        double rc4 = riskScores.get("rc4");

        // 최종 인지 저하 의심 지수 (S_cog) 계산
        double sCog = Math.min(1.0, rc1 + rc2 + rc3 + rc4);
        double finalScore = sCog * 35;
        log.info("[CognitiveScoreCalculator] 최종 계산된 점수: {}", finalScore);

        // 최종 점수 반환
        return finalScore;
    }

    //C1. 핑퐁 이동: 서로 다른 두 방을 30분 이내에 5회 이상 번갈아 켠 경우
    private int calculatePingPongMovement(List<LedHistory> events) {
        List<LedHistory> onEvents = events.stream()
                .filter(e -> e.getOnOff() == OnOff.ON)
                .toList();

        int pingPongCount = 0;
        if (onEvents.size() < 5) {
            return 0;
        }

        for (int i = 0; i <= onEvents.size() - 5; i++) {
            LedHistory first = onEvents.get(i);
            LedHistory second = onEvents.get(i + 1);
            LedHistory third = onEvents.get(i + 2);
            LedHistory fourth = onEvents.get(i + 3);
            LedHistory fifth = onEvents.get(i + 4);

            // 두 개의 방이 번갈아 발생하는 패턴 확인 (A -> B -> A -> B -> A)
            boolean isPatternMatch = !first.getSensorGbn().equals(second.getSensorGbn()) &&
                    first.getSensorGbn().equals(third.getSensorGbn()) &&
                    second.getSensorGbn().equals(fourth.getSensorGbn()) &&
                    first.getSensorGbn().equals(fifth.getSensorGbn());

            if (isPatternMatch) {
                // 첫번째 이벤트와 다섯번째 이벤트가 30분 이내에 발생했는지 확인
                if (Duration.between(first.getEventTime(), fifth.getEventTime()).toMinutes() <= 30) {
                    pingPongCount++;
                    // 중복 계산을 피하기 위해 다음 5개 이벤트를 건너뜀
                    i += 4;
                }
            }
        }
        return pingPongCount;
    }

    //C2. 재진입 반복: 같은 방을 10분 이내에 3번 이상 On/Off 반복한 경우
    private int calculateRepetitiveReentry(List<LedHistory> events) {
        Map<SensorGbn, List<LedHistory>> onEventsByRoom = events.stream()
                .filter(e -> e.getOnOff() == OnOff.ON)
                .collect(Collectors.groupingBy(LedHistory::getSensorGbn));

        int reentryCount = 0;
        for (List<LedHistory> roomEvents : onEventsByRoom.values()) {
            if (roomEvents.size() < 3) {
                continue;
            }
            for (int i = 0; i <= roomEvents.size() - 3; i++) {
                LedHistory first = roomEvents.get(i);
                LedHistory third = roomEvents.get(i + 2);

                // 10분 이내에 3번의 ON 이벤트가 발생했는지 확인
                if (Duration.between(first.getEventTime(), third.getEventTime()).toMinutes() <= 10) {
                    reentryCount++;
                    // 중복 계산을 피하기 위해 다음 3개 이벤트를 건너뜀
                    i += 2;
                }
            }
        }
        return reentryCount;
    }

    //C3. 소등 잊음: 주간(07:00~21:59) 동안 부엌·거실 불을 120분 이상 켜둔 경우
    private int calculateForgettingToTurnOffLights(List<LedHistory> events) {
        List<LedHistory> targetEvents = events.stream()
                .filter(e -> e.getSensorGbn() == SensorGbn.KITCHEN || e.getSensorGbn() == SensorGbn.LIVING_ROOM)
                .toList();

        Map<SensorGbn, LocalDateTime> openEvents = new HashMap<>();
        int forgotCount = 0;

        LocalTime dayStart = LocalTime.of(7, 0);
        LocalTime dayEnd = LocalTime.of(21, 59);

        for (LedHistory event : targetEvents) {
            if (event.getOnOff() == OnOff.ON) {
                openEvents.put(event.getSensorGbn(), event.getEventTime());
            } else if (event.getOnOff() == OnOff.OFF && openEvents.containsKey(event.getSensorGbn())) {
                LocalDateTime onTime = openEvents.remove(event.getSensorGbn());
                LocalTime onTimeLocal = onTime.toLocalTime();

                // ON 이벤트가 주간에 발생했는지 확인
                if (!onTimeLocal.isBefore(dayStart) && !onTimeLocal.isAfter(dayEnd)) {
                    long durationMinutes = Duration.between(onTime, event.getEventTime()).toMinutes();
                    if (durationMinutes >= 120) {
                        forgotCount++;
                    }
                }
            }
        }
        return forgotCount;
    }

    //C4. 야간 순회: 23:00~익일 07:00 사이에 3개 이상의 방을 10분 간격으로 연속 방문한 경우
    private int calculateNightWandering(List<LedHistory> events) {
        if (events.isEmpty()) {
            return 0;
        }
        LocalDate analysisDate = events.get(0).getEventTime().toLocalDate();
        LocalDateTime nightStart = analysisDate.atTime(LocalTime.of(23, 0));
        LocalDateTime nightEnd = analysisDate.plusDays(1).atTime(LocalTime.of(7, 0));

        List<LedHistory> nightOnEvents = events.stream()
                .filter(e -> e.getOnOff() == OnOff.ON)
                .filter(e -> {
                    LocalDateTime eventTime = e.getEventTime();
                    return !eventTime.isBefore(nightStart) && eventTime.isBefore(nightEnd);
                })
                .toList();

        int wanderingCount = 0;
        if (nightOnEvents.size() < 3) {
            return 0;
        }

        for (int i = 0; i <= nightOnEvents.size() - 3; i++) {
            LedHistory first = nightOnEvents.get(i);
            LedHistory second = nightOnEvents.get(i + 1);
            LedHistory third = nightOnEvents.get(i + 2);

            // 3개의 방이 모두 다른지 확인
            boolean areRoomsDistinct = !first.getSensorGbn().equals(second.getSensorGbn()) &&
                    !first.getSensorGbn().equals(third.getSensorGbn()) &&
                    !second.getSensorGbn().equals(third.getSensorGbn());

            if (areRoomsDistinct) {
                // 연속 방문 간격이 10분 이내인지 확인
                boolean isWithinTime = Duration.between(first.getEventTime(), second.getEventTime()).toMinutes() <= 10 &&
                        Duration.between(second.getEventTime(), third.getEventTime()).toMinutes() <= 10;
                if (isWithinTime) {
                    wanderingCount++;
                    // 중복 계산을 피하기 위해 다음 3개 이벤트를 건너뜀
                    i += 2;
                }
            }
        }
        return wanderingCount;
    }


    //각 항목별 횟수를 가이드라인에 따라 위험지수(rc1, rc2, rc3, rc4)로 변환
    private Map<String, Double> convertCountsToRiskScores(int c1Count, int c2Count, int c3Count, int c4Count) {
        double rc1, rc2, rc3, rc4;

        // C1. 핑퐁 이동 -> rc1
        if (c1Count == 1) rc1 = 0.20;
        else if (c1Count == 2) rc1 = 0.35;
        else if (c1Count >= 3) rc1 = 0.45; // 3-4건, 5건 이상 모두 0.45
        else rc1 = 0.0;

        // C2. 재진입 반복 -> rc2
        if (c2Count == 1) rc2 = 0.10;
        else if (c2Count == 2) rc2 = 0.18;
        else if (c2Count == 3) rc2 = 0.22;
        else if (c2Count >= 4) rc2 = 0.25;
        else rc2 = 0.0;

        // C3. 소등 잊음 -> rc3
        if (c3Count == 1) rc3 = 0.12;
        else if (c3Count == 2) rc3 = 0.24;
        else if (c3Count == 3) rc3 = 0.30;
        else if (c3Count >= 4) rc3 = 0.35;
        else rc3 = 0.0;

        // C4. 야간 순회 -> rc4
        if (c4Count == 1) rc4 = 0.20;
        else if (c4Count == 2) rc4 = 0.27;
        else if (c4Count >= 3) rc4 = 0.30;
        else rc4 = 0.0;

        Map<String, Double> scores = new HashMap<>();
        scores.put("rc1", rc1);
        scores.put("rc2", rc2);
        scores.put("rc3", rc3);
        scores.put("rc4", rc4);
        return scores;
    }
}
