package org.farm.fireflyserver.domain.led.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.led.persistence.AnomalyType;
import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;
import org.farm.fireflyserver.domain.led.persistence.repository.LedHistoryRepository;
import org.farm.fireflyserver.domain.led.service.calculator.ScoreCalculator;
import org.farm.fireflyserver.domain.senior.service.SeniorService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnomalyAnalysisService {
    private final LedHistoryRepository ledHistoryRepository;
    private final List<ScoreCalculator> calculators;
    private final SeniorService seniorService;

    private Map<AnomalyType, ScoreCalculator> calculatorMap;

    @PostConstruct
    public void init() {
        calculatorMap = calculators.stream()
                .collect(Collectors.toMap(ScoreCalculator::getAnomalyType, Function.identity()));
    }

    @Transactional
    public void analyzeSleepRiskForDevice(String ledMtchnSn, LocalDate analysisDate) {
        //1. 데이터 조회: 분석일 00:00 ~ 다음날 06:59
        LocalDateTime dataStart = analysisDate.atStartOfDay();
        LocalDateTime dataEnd = analysisDate.plusDays(1).atTime(LocalTime.of(6, 59, 59));

        List<LedHistory> allEvents = ledHistoryRepository
                .findByLedMtchnSnAndEventTimeBetweenOrderByEventTimeAsc(ledMtchnSn, dataStart, dataEnd);

        Map<AnomalyType, Double> scoreMap = new HashMap<>();

        //2. 점수 계산
        for(AnomalyType type : calculatorMap.keySet()) {
            ScoreCalculator scoreCalculator = calculatorMap.get(type);
            Double scoreResult = scoreCalculator.calculate(allEvents);

            scoreMap.put(type, scoreResult);
        }

        //3. 결과 저장
        Double sleepScr = scoreMap.getOrDefault(AnomalyType.SLEEP, 0.0);
        Double memoryScr = scoreMap.getOrDefault(AnomalyType.COGNITIVE, 0.0);
        Double lowEngScr = scoreMap.getOrDefault(AnomalyType.LOWENG, 0.0);
        Double dangerRt = sleepScr + memoryScr + lowEngScr;

        seniorService.updateSeniorStatus(ledMtchnSn, sleepScr, memoryScr, lowEngScr, dangerRt);
    }
}