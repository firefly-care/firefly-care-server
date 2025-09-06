package org.farm.fireflyserver.domain.led.service.batch;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;
import org.farm.fireflyserver.domain.led.persistence.entity.OnOff;
import org.farm.fireflyserver.domain.led.persistence.repository.LedHistoryRepository;
import org.farm.fireflyserver.domain.led.web.dto.response.LedDataLogDto;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Job 종료 후, 최신 ON 이벤트가 Processor에 없으면 OFF 이벤트 추가
@RequiredArgsConstructor
@Slf4j
public class LedJobListener extends JobExecutionListenerSupport {

    private final LedHistoryRepository ledHistoryRepository;
    private final Map<String, LedDataLogDto> latestMap;
    private final Map<String, LocalDateTime> windowTimes;

    @PersistenceContext
    private EntityManager entityManager;

    // CASE 4: DB 있음(ON) → Processor 없음 → OFF 이벤트 추가
    @Override
    public void afterJob(JobExecution jobExecution) {

        final LocalDateTime now = windowTimes.get("now");

        entityManager.clear();  // 캐시 초기화

        // 모든 LED센서 최신 데이터 조회
        List<LedHistory> allHistories = ledHistoryRepository.findLatestHistories();

        //로그
        allHistories.forEach(h ->
                log.info("[Listener] latest id={}, sn={}, gbn={}, onOff={}, eventTime={}",
                        h.getLedHistoryId(), h.getLedMtchnSn(), h.getSensorGbn(),
                        h.getOnOff(), h.getEventTime())
        );

        for (LedHistory history : allHistories) {
            String key = history.getLedMtchnSn() + "_" + history.getSensorGbn().getCode();

            // 스킵
            if (history.getOnOff() != OnOff.ON || latestMap.containsKey(key)) {
                continue;
            }

            boolean offExists = ledHistoryRepository.existsByLedMtchnSnAndSensorGbnAndOnOffAndEventTime(
                    history.getLedMtchnSn(), history.getSensorGbn(), OnOff.OFF, now);
            if (offExists) {
                continue;
            }

            // OFF 이벤트 추가
            LedHistory offHistory = LedHistory.builder()
                    .ledMtchnSn(history.getLedMtchnSn())
                    .sensorGbn(history.getSensorGbn())
                    .onOff(OnOff.OFF)
                    .eventTime(now)
                    .build();
            ledHistoryRepository.save(offHistory);
        }
    }
}
