package org.farm.fireflyserver.domain.led.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;
import org.farm.fireflyserver.domain.led.persistence.repository.LedHistoryRepository;
import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;
import org.farm.fireflyserver.domain.senior.persistence.entity.SeniorStatus;
import org.farm.fireflyserver.domain.senior.persistence.repository.SeniorRepository;
import org.farm.fireflyserver.domain.senior.persistence.repository.SeniorStatusRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RiskAnalysisService {
    private final LedHistoryRepository ledHistoryRepository;
    private final SleepScoreCalculator sleepScoreCalculator;
    private final SeniorStatusRepository seniorStatusRepository;
    private final SeniorRepository seniorRepository;

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
        Optional<Senior> seniorOptional = seniorRepository.findByLedMtchnSn(ledMtchnSn);
        if (seniorOptional.isPresent()) {
            Senior senior = seniorOptional.get();
            SeniorStatus seniorStatus = senior.getSeniorStatus();
            if (seniorStatus != null) {
                Double finalScore = (Double) scoreResult.get("finalScore");
                seniorStatus.updateSleepScore(finalScore);
                seniorStatusRepository.save(seniorStatus);
            } else {
                System.out.println("해당 어르신에 대한 상태 정보(SeniorStatus)가 없습니다: " + senior.getName());
            }
        } else {
            System.out.println("해당 LED 장치 번호와 일치하는 어르신 정보가 없습니다: " + ledMtchnSn);
        }
    }
}
