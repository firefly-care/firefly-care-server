package org.farm.fireflyserver.domain.led.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("AnomalyAnalysisService 초기화 완료. 등록된 Calculator 수: {}", calculatorMap.size());
    }

    @Transactional
    public void analyzeSleepRiskForDevice(String ledMtchnSn, LocalDate analysisDate) {
        log.info("분석 시작: [ledMtchnSn={}, analysisDate={}]", ledMtchnSn, analysisDate);
        //1. 데이터 조회: 분석일 00:00 ~ 다음날 06:59
        LocalDateTime dataStart = analysisDate.atStartOfDay();
        LocalDateTime dataEnd = analysisDate.plusDays(1).atTime(LocalTime.of(6, 59, 59));

        List<LedHistory> allEvents = ledHistoryRepository
                .findByLedMtchnSnAndEventTimeBetweenOrderByEventTimeAsc(ledMtchnSn, dataStart, dataEnd);
        log.info("데이터베이스에서 {}개의 이벤트 조회 완료.", allEvents.size());

        Map<AnomalyType, Double> scoreMap = new HashMap<>();

        //2. 점수 계산
        for(AnomalyType type : calculatorMap.keySet()) {
            ScoreCalculator scoreCalculator = calculatorMap.get(type);
            log.info("{} 타입 점수 계산 시작...", type);
            Double scoreResult = scoreCalculator.calculate(allEvents);
            log.info("{} 타입 점수 계산 완료. 결과: {}", type, scoreResult);
            scoreMap.put(type, scoreResult);
        }

        log.debug("모든 점수 계산 완료. scoreMap: {}", scoreMap);

        //3. 결과 저장
        Double sleepScr = scoreMap.getOrDefault(AnomalyType.SLEEP, 0.0);
        Double memoryScr = scoreMap.getOrDefault(AnomalyType.COGNITIVE, 0.0);
        Double lowEngScr = scoreMap.getOrDefault(AnomalyType.LOWENG, 0.0);
        Double dangerRt = sleepScr + memoryScr + lowEngScr;
        log.info("최종 점수 집계: [sleepScr={}, memoryScr={}, lowEngScr={}, dangerRt={}]", sleepScr, memoryScr, lowEngScr, dangerRt);

        log.info("SeniorService.updateSeniorStatus 호출...");
        seniorService.updateSeniorStatus(ledMtchnSn, sleepScr, memoryScr, lowEngScr, dangerRt);
        log.info("분석 및 상태 업데이트 완료: [ledMtchnSn={}]", ledMtchnSn);
    }
}