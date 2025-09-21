package org.farm.fireflyserver.domain.led.service.batch;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;
import org.farm.fireflyserver.domain.led.persistence.entity.LedState;
import org.farm.fireflyserver.domain.led.persistence.entity.OnOff;
import org.farm.fireflyserver.domain.led.persistence.repository.LedHistoryRepository;
import org.farm.fireflyserver.domain.led.persistence.repository.LedStateRepository;
import org.farm.fireflyserver.domain.led.web.dto.response.LedDataLogDto;
import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Job 종료 후 실행
// CASE4 : 센서별 최신 히스토리가 ON인데, Processor에 없는 경우 → OFF 히스토리 기록 + 현재 상태를 OFF로 전환
@RequiredArgsConstructor
public class LedJobListener extends JobExecutionListenerSupport {

    private final LedHistoryRepository ledHistoryRepository;
    private final LedStateRepository ledStateRepository;
    private final Map<String, LedDataLogDto> latestMap;
    private final Map<String, LocalDateTime> windowTimes;

    @Override
    public void afterJob(JobExecution jobExecution) {
        final LocalDateTime now = windowTimes.get("now");
        final Set<String> observed = latestMap.keySet();

        List<LedHistory> latestPerSensor = ledHistoryRepository.findLatestHistories();
        List<LedHistory> offHistories = new ArrayList<>();
        List<LedState> statesToSave = new ArrayList<>();

        for (LedHistory latest : latestPerSensor) {
            // 최신 상태가 ON이 아니거나, 이번 윈도우에서 관측된 경우는 스킵
            if (latest.getOnOff() != OnOff.ON) {
                continue;
            }
            String key = latest.getLedMtchnSn() + "_" + latest.getSensorGbn().getCode();
            if (observed.contains(key)) {
                continue;
            }

            // OFF 히스토리 추가
            boolean alreadyOff = ledHistoryRepository.existsByLedMtchnSnAndSensorGbnAndOnOffAndEventTime(latest.getLedMtchnSn(), latest.getSensorGbn(), OnOff.OFF, now
            );
            if (!alreadyOff) {
                offHistories.add(
                        LedHistory.builder()
                                .ledMtchnSn(latest.getLedMtchnSn())
                                .sensorGbn(latest.getSensorGbn())
                                .onOff(OnOff.OFF)
                                .eventTime(now)
                                .build()
                );
            }

            // LED 현재 상태 업데이트
            LedState state = ledStateRepository.findByLedMtchnSnAndSensorGbn(latest.getLedMtchnSn(), latest.getSensorGbn())
                    .orElseGet(() ->
                            LedState.builder()
                                    .ledMtchnSn(latest.getLedMtchnSn())
                                    .sensorGbn(latest.getSensorGbn())
                                    .onOff(OnOff.OFF)
                                    .build()
                    );

            if (state.getOnOff() != OnOff.OFF) {
                state.updateState(OnOff.OFF);
            }
            statesToSave.add(state);
        }

        if (!offHistories.isEmpty()) ledHistoryRepository.saveAll(offHistories);
        if (!statesToSave.isEmpty()) ledStateRepository.saveAll(statesToSave);

    }

}
