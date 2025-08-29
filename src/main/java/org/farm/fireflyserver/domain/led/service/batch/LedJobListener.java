package org.farm.fireflyserver.domain.led.service.batch;

import lombok.RequiredArgsConstructor;
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
public class LedJobListener extends JobExecutionListenerSupport {

    private final LedHistoryRepository ledHistoryRepository;
    private final Map<String, LedDataLogDto> latestMap;

    // CASE 4: DB 있음(ON) → Processor 없음 → OFF 이벤트 추가
    @Override
    public void afterJob(JobExecution jobExecution) {
        List<LedHistory> allHistories = ledHistoryRepository.findLatestHistories();

        for (LedHistory history : allHistories) {
            String key = history.getLedMtchnSn() + "_" + history.getSensorGbn().getCode();
            if (!latestMap.containsKey(key) && history.getOnOff() == OnOff.ON) {
                LedHistory offHistory = LedHistory.builder()
                        .ledMtchnSn(history.getLedMtchnSn())
                        .sensorGbn(history.getSensorGbn())
                        .onOff(OnOff.OFF)
                        .eventTime(LocalDateTime.now())
                        .build();
                ledHistoryRepository.save(offHistory);
            }
        }
    }
}

