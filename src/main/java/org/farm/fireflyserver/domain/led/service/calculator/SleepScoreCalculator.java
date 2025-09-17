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

@Slf4j
@Component
public class SleepScoreCalculator implements ScoreCalculator{
    @Override
    public AnomalyType getAnomalyType() {
        return AnomalyType.SLEEP;
    }
    
    public Double calculate(List<LedHistory> events) {
        log.info("[SleepScoreCalculator] 계산 시작. 입력된 이벤트 수: {}", events != null ? events.size() : 0);
        // 데이터가 없는 경우, 오류 방지를 위해 0점 처리
        if (events == null || events.isEmpty()) {
            log.warn("[SleepScoreCalculator] 입력된 이벤트가 없어 0.0을 반환합니다.");
            return 0.0;
        }

        // 분석 기준이 되는 날짜를 첫 번째 이벤트에서 추출
        LocalDate analysisDate = events.get(0).getEventTime().toLocalDate();

        // 야간 시간대를 정의 (분석일 23:00 ~ 다음날 07:00 이전)
        LocalDateTime nightStart = analysisDate.atTime(LocalTime.of(23, 0));
        LocalDateTime nightEnd = analysisDate.plusDays(1).atTime(LocalTime.of(7, 0));

        // 전체 이벤트에서 야간 시간대에 해당하는 이벤트만 필터링
        List<LedHistory> nightEvents = events.stream()
                .filter(event -> {
                    LocalDateTime eventTime = event.getEventTime();
                    return !eventTime.isBefore(nightStart) && eventTime.isBefore(nightEnd);
                })
                .toList();
        log.info("[SleepScoreCalculator] 야간 이벤트 필터링 완료. 야간 이벤트 수: {}", nightEvents.size());

        // 야간에 발생한 이벤트가 없으면 수면 점수 0점 (가장 좋은 상태)
        if (nightEvents.isEmpty()){
            log.info("[SleepScoreCalculator] 야간 이벤트가 없어 0.0을 반환합니다.");
            return 0.0;
        }

        int n1Count = calculateNightlyActivations(nightEvents);
        int n2Count = calculateShortToggles(nightEvents);
        int n3Count = calculateFragmentedBlocks(nightEvents);
        log.info("[SleepScoreCalculator] 중간 계산 완료: [n1Count={}, n2Count={}, n3Count={}]", n1Count, n2Count, n3Count);

        // 횟수를 점수로 변환
        Map<String, Double> riskScores = convertCountsToRiskScores(n1Count, n2Count, n3Count);
        
        //각 횟수별 위험도 점수
        double rs1 = riskScores.get("rs1");
        double rs2 = riskScores.get("rs2");
        double rs3 = riskScores.get("rs3");

        //최종 수면 점수
        double sSleep = Math.min(1.0, rs1 + rs2 + rs3);
        double finalScore = sSleep * 45;
        log.info("[SleepScoreCalculator] 최종 계산된 점수: {}", finalScore);
        
        //최종 점수
        return finalScore;
    }

    // N1. 야간 활성 횟수
    private int calculateNightlyActivations(List<LedHistory> events) {
        return (int) events.stream()
                .filter(e -> e.getOnOff() == OnOff.ON)
                .count();
    }

    // N2. 야간 짧은 토글 수
    private int calculateShortToggles(List<LedHistory> events) {
        int toggleCount = 0;
        for (int i = 0; i < events.size() - 1; i++) {
            LedHistory current = events.get(i);
            LedHistory next = events.get(i + 1);

            if (current.getOnOff() == OnOff.ON &&
                    next.getOnOff() == OnOff.OFF &&
                    current.getSensorGbn().equals(next.getSensorGbn()) &&
                    Duration.between(current.getEventTime(), next.getEventTime()).toMinutes() <= 2) {
                toggleCount++;
            }
        }
        return toggleCount;
    }

    // N3. 분절 블록 수
    private int calculateFragmentedBlocks(List<LedHistory> events) {
        int blockCount = 0;
        List<LedHistory> targetEvents = events.stream()
                .filter(e -> e.getSensorGbn() == SensorGbn.BED_ROOM || e.getSensorGbn() == SensorGbn.TOILET)
                .toList();

        Map<SensorGbn, LocalDateTime> openEvents = new HashMap<>();

        for (LedHistory event : targetEvents) {
            if (event.getOnOff() == OnOff.ON) {
                openEvents.put(event.getSensorGbn(), event.getEventTime());
            } else if (event.getOnOff() == OnOff.OFF && openEvents.containsKey(event.getSensorGbn())) {
                LocalDateTime onTime = openEvents.remove(event.getSensorGbn());
                long durationMinutes = Duration.between(onTime, event.getEventTime()).toMinutes();
                if (durationMinutes >= 5 && durationMinutes <= 30) {
                    blockCount++;
                }
            }
        }
        return blockCount;
    }

    //각 항목별 횟수를 가이드라인에 따라 위험지수(rs1, rs2, rs3)로 변환
    private Map<String, Double> convertCountsToRiskScores(int n1Count, int n2Count, int n3Count) {
        double rs1, rs2, rs3;

        // N1. 야간 활성 횟수 -> rs1
        if (n1Count <= 1) rs1 = 0.00;
        else if (n1Count == 2) rs1 = 0.25;
        else if (n1Count == 3) rs1 = 0.35;
        else if (n1Count == 4) rs1 = 0.50;
        else if (n1Count == 5) rs1 = 0.60;
        else if (n1Count <= 7) rs1 = 0.70;
        else rs1 = 0.80;

        // N2. 야간 짧은 토글 수 -> rs2
        if (n2Count <= 2) rs2 = 0.00;
        else if (n2Count <= 4) rs2 = 0.15;
        else if (n2Count <= 6) rs2 = 0.22;
        else if (n2Count <= 8) rs2 = 0.30;
        else if (n2Count <= 10) rs2 = 0.38;
        else if (n2Count <= 12) rs2 = 0.42;
        else rs2 = 0.45;

        // N3. 분절 블록 수 -> rs3
        if (n3Count == 0) rs3 = 0.00;
        else if (n3Count == 1) rs3 = 0.12;
        else if (n3Count <= 3) rs3 = 0.20;
        else rs3 = 0.25;

        Map<String, Double> scores = new HashMap<>();
        scores.put("rs1", rs1);
        scores.put("rs2", rs2);
        scores.put("rs3", rs3);
        return scores;
    }
}