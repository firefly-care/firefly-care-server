package org.farm.fireflyserver.domain.led.service.calculator;

import lombok.extern.slf4j.Slf4j;
import org.farm.fireflyserver.domain.led.persistence.AnomalyType;
import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;
import org.farm.fireflyserver.domain.led.persistence.entity.OnOff;

import org.farm.fireflyserver.domain.led.persistence.entity.SensorGbn;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LowEngScoreCalculator implements ScoreCalculator {

    @Override
    public AnomalyType getAnomalyType() {
        return AnomalyType.LOWENG;
    }

    public Double calculate(List<LedHistory> allEvents) {
        log.info("[LowEngScoreCalculator] 계산 시작. 입력된 이벤트 수: {}", allEvents != null ? allEvents.size() : 0);
        if (allEvents == null || allEvents.isEmpty()) {
            log.warn("[LowEngScoreCalculator] 입력된 이벤트가 없어 0.0을 반환합니다.");
            return 0.0;
        }

        // 주간(07:00 ~ 21:59) 이벤트만 필터링
        LocalTime dayStart = LocalTime.of(7, 0);
        LocalTime dayEnd = LocalTime.of(21, 59, 59);

        List<LedHistory> daytimeEvents = allEvents.stream()
                .filter(e -> {
                    LocalTime eventTime = e.getEventTime().toLocalTime();
                    return !eventTime.isBefore(dayStart) && !eventTime.isAfter(dayEnd);
                })
                .toList();
        log.info("[LowEngScoreCalculator] 주간 이벤트 필터링 완료. 주간 이벤트 수: {}", daytimeEvents.size());

        // 각 기준별 값 계산
        long l1TotalTime = calculateTotalLightingTime(daytimeEvents);
        int l2ActivationCount = calculateActivationCount(daytimeEvents);
        int l3UniqueRoomCount = calculateUniqueRoomCount(daytimeEvents);
        log.info("[LowEngScoreCalculator] 중간 계산 완료: [l1TotalTime={}, l2ActivationCount={}, l3UniqueRoomCount={}]", l1TotalTime, l2ActivationCount, l3UniqueRoomCount);

        // 계산된 값을 위험 지수(rl)로 변환
        Map<String, Double> riskScores = convertValuesToRiskScores(l1TotalTime, l2ActivationCount, l3UniqueRoomCount);

        // 각 기준별 위험 지수
        double rl1 = riskScores.get("rl1");
        double rl2 = riskScores.get("rl2");
        double rl3 = riskScores.get("rl3");

        // 최종 무기력증 의심 지수 (S_leth) 계산
        double sLeth = Math.min(1.0, rl1 + rl2 + rl3);
        double finalScore = sLeth * 20;
        log.info("[LowEngScoreCalculator] 최종 계산된 점수: {}", finalScore);

        // 최종 점수 반환
        return finalScore;
    }

    /**
     * L1. 총 점등 시간: 하루 동안 집안의 불이 켜져 있던 총 시간 (분 단위)
     */
    private long calculateTotalLightingTime(List<LedHistory> daytimeEvents) {
        long totalMinutes = 0;
        Map<SensorGbn, LocalDateTime> onEventsMap = new HashMap<>();

        for (LedHistory event : daytimeEvents) {
            if (event.getOnOff() == OnOff.ON) {
                // 이전에 켜진 기록이 있다면 OFF 없이 다시 켜진 것이므로 이전 기록을 무시하고 새로 시작
                onEventsMap.put(event.getSensorGbn(), event.getEventTime());
            } else if (event.getOnOff() == OnOff.OFF && onEventsMap.containsKey(event.getSensorGbn())) {
                LocalDateTime onTime = onEventsMap.remove(event.getSensorGbn());
                totalMinutes += Duration.between(onTime, event.getEventTime()).toMinutes();
            }
        }
        return totalMinutes;
    }

    /**
     * L2. 활성 횟수: 하루 동안 불을 켠 횟수
     */
    private int calculateActivationCount(List<LedHistory> daytimeEvents) {
        return (int) daytimeEvents.stream()
                .filter(e -> e.getOnOff() == OnOff.ON)
                .count();
    }

    /**
     * L3. 고유 방 수: 하루 동안 점등이 있었던 서로 다른 방의 개수
     */
    private int calculateUniqueRoomCount(List<LedHistory> daytimeEvents) {
        Set<SensorGbn> uniqueRooms = daytimeEvents.stream()
                .filter(e -> e.getOnOff() == OnOff.ON)
                .map(LedHistory::getSensorGbn)
                .collect(Collectors.toSet());
        return uniqueRooms.size();
    }

    /**
     * 각 항목별 값을 가이드라인에 따라 위험지수(rl1, rl2, rl3)로 변환
     */
    private Map<String, Double> convertValuesToRiskScores(long l1TotalTime, int l2ActivationCount, int l3UniqueRoomCount) {
        double rl1, rl2, rl3;

        // L1. 총 점등 시간 -> rl1
        if (l1TotalTime >= 180) rl1 = 0.00;
        else if (l1TotalTime >= 150) rl1 = 0.10;
        else if (l1TotalTime >= 120) rl1 = 0.20;
        else if (l1TotalTime >= 90) rl1 = 0.30;
        else if (l1TotalTime >= 60) rl1 = 0.40;
        else if (l1TotalTime >= 30) rl1 = 0.50;
        else rl1 = 0.60;

        // L2. 활성 횟수 -> rl2
        if (l2ActivationCount >= 21) rl2 = 0.00;
        else if (l2ActivationCount >= 16) rl2 = 0.02;
        else if (l2ActivationCount >= 11) rl2 = 0.05;
        else if (l2ActivationCount >= 7) rl2 = 0.15;
        else if (l2ActivationCount >= 4) rl2 = 0.20;
        else rl2 = 0.25; // 0-3회

        // L3. 고유 방 수 -> rl3
        if (l3UniqueRoomCount >= 4) rl3 = 0.00;
        else if (l3UniqueRoomCount == 3) rl3 = 0.02;
        else if (l3UniqueRoomCount == 2) rl3 = 0.08;
        else rl3 = 0.15; // 0-1개

        Map<String, Double> scores = new HashMap<>();
        scores.put("rl1", rl1);
        scores.put("rl2", rl2);
        scores.put("rl3", rl3);
        return scores;
    }
}
