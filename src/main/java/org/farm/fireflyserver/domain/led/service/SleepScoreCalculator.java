package org.farm.fireflyserver.domain.led.service;

import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;
import org.farm.fireflyserver.domain.led.persistence.entity.OnOff;
import org.farm.fireflyserver.domain.led.persistence.entity.SensorGbn;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SleepScoreCalculator {
    public Map<String, Object> calculate(List<LedHistory> nightEvents) {
        int n1Count = calculateNightlyActivations(nightEvents);
        int n2Count = calculateShortToggles(nightEvents);
        int n3Count = calculateFragmentedBlocks(nightEvents);

        // 횟수를 점수로 변환
        Map<String, Double> riskScores = convertCountsToRiskScores(n1Count, n2Count, n3Count);
        
        //각 횟수별 위험도 점수
        double rs1 = riskScores.get("rs1");
        double rs2 = riskScores.get("rs2");
        double rs3 = riskScores.get("rs3");

        //최종 수면 점수
        double sSleep = Math.min(1.0, rs1 + rs2 + rs3);
        //최종 점수
        double finalScore = sSleep * 45;

        // 상세 결과를 Map으로 저장
        Map<String, Object> details = new HashMap<>();
        details.put("n1_count", n1Count);
        details.put("n2_count", n2Count);
        details.put("n3_count", n3Count);

        Map<String, Object> result = new HashMap<>();
        result.put("finalScore", finalScore);
        result.put("sSleep", sSleep);
        result.put("details", details);

        return result;
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