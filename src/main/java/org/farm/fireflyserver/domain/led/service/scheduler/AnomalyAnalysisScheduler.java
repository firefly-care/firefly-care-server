package org.farm.fireflyserver.domain.led.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.farm.fireflyserver.domain.led.service.AnomalyAnalysisService;
import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;
import org.farm.fireflyserver.domain.senior.persistence.repository.SeniorRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnomalyAnalysisScheduler {
    private final AnomalyAnalysisService riskAnalysisService;
    private final SeniorRepository seniorRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    public void runDailyAnalysis() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("===== " + yesterday + "의 일일 이상 징후 분석을 시작합니다. =====");

        List<Senior> seniors = seniorRepository.findAll();

        for(Senior senior : seniors) {
            String ledMtchnSn = senior.getLedMtchnSn();
            if(ledMtchnSn == null || ledMtchnSn.isBlank()) {
                log.warn("Senior ID: {} 이름: {}님은 LED 장치 번호가 없어 분석을 건너뜁니다.", senior.getSeniorId(), senior.getName());
                continue;
            }
            try {
                log.info("Senior ID: {}, 이름: {}에 대한 분석을 시작합니다.", senior.getSeniorId(), senior.getName());
                riskAnalysisService.analyzeSleepRiskForDevice(ledMtchnSn, yesterday);
            } catch (Exception e) {
                log.error(ledMtchnSn + "의 LED 데이터 분석 실패: ", e);
            }
        }

        log.info("===== 일일 이상징후 분석 종료 =====");
    }
}
