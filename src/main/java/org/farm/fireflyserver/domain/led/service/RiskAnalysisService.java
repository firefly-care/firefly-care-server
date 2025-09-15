package org.farm.fireflyserver.domain.led.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;
import org.farm.fireflyserver.domain.led.persistence.repository.LedHistoryRepository;
import org.farm.fireflyserver.domain.senior.service.SeniorService;
import org.farm.fireflyserver.domain.senior.web.dto.request.RequestSeniorDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RiskAnalysisService {
    private final LedHistoryRepository ledHistoryRepository;
    private final SleepScoreCalculator sleepScoreCalculator;
    private final SeniorService seniorService;

    @Transactional
    public void analyzeSleepRiskForDevice(String ledMtchnSn, LocalDate analysisDate) {
        //1. 데이터 조회: 분석일 00:00 ~ 다음날 06:59
        LocalDateTime dataStart = analysisDate.atStartOfDay();
        LocalDateTime dataEnd = analysisDate.plusDays(1).atTime(LocalTime.of(6, 59, 59));

        List<LedHistory> allEvents = ledHistoryRepository
                .findByLedMtchnSnAndEventTimeBetweenOrderByEventTimeAsc(ledMtchnSn, dataStart, dataEnd);

        //2. 야간 데이터 필터링: 분석일 23:00 ~ 다음날 06:59
        LocalDateTime nightStart = analysisDate.atTime(LocalTime.of(23, 0));
        List<LedHistory> nightEvents = allEvents.stream()
                .filter(event -> !event.getEventTime().isBefore(nightStart))
                .toList();

        if (nightEvents.isEmpty()){
            System.out.println("해당 LED 장치에 야간 데이터가 없습니다. " + ledMtchnSn);
            return;
        }

        //3. 점수 계산
        Map<String, Object> scoreResult = sleepScoreCalculator.calculate(nightEvents);

        //4. 결과 저장
        Double finalScore = (Double) scoreResult.get("finalScore");
        seniorService.updateSleepScore(new RequestSeniorDto.UpdateSleepScore(ledMtchnSn, finalScore));
    }
}