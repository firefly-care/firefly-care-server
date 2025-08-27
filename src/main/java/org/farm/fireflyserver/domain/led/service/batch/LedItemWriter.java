package org.farm.fireflyserver.domain.led.service.batch;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;
import org.farm.fireflyserver.domain.led.persistence.entity.OnOff;
import org.farm.fireflyserver.domain.led.persistence.entity.SensorGbn;
import org.farm.fireflyserver.domain.led.persistence.repository.LedHistoryRepository;
import org.farm.fireflyserver.domain.led.web.dto.response.LedDataLogDto;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 이벤트 기반 LED 센서 로그를 DB에 저장하는 ItemWriter
@RequiredArgsConstructor
public class LedItemWriter implements ItemWriter<Map<String, LedDataLogDto>> {

    private final LedHistoryRepository ledHistoryRepository;

    @Override
    public void write(Chunk<? extends Map<String, LedDataLogDto>> chunk) throws Exception {

        // Processor에서 넘어온 최신 로그
        Map<String, LedDataLogDto> latestMap = new HashMap<>();
        for (Map<String, LedDataLogDto> map : chunk) {
            for (Map.Entry<String, LedDataLogDto> entry : map.entrySet()) {
                String key = entry.getKey();
                LedDataLogDto dto = entry.getValue();
                LedDataLogDto existing = latestMap.get(key);
                if (existing == null || existing.eventTime().isBefore(dto.eventTime())) {
                    latestMap.put(key, dto);
                }
            }
        }

        for (Map.Entry<String, LedDataLogDto> entry : latestMap.entrySet()) {
            String key = entry.getKey();
            LedDataLogDto dto = entry.getValue();

            String[] parts = key.split("_");
            String ledMtchnSn = parts[0];
            SensorGbn sensorGbn = SensorGbn.fromCode(parts[1]);

            LedHistory latestHistory = ledHistoryRepository
                    .findTopByLedMtchnSnAndSensorGbnOrderByEventTimeDesc(ledMtchnSn, sensorGbn);

            if (latestHistory != null) {

                // 중복 제거
                if (latestHistory.getOnOff() == dto.onOff() &&
                        latestHistory.getEventTime().isEqual(dto.eventTime())) {
                    continue;
                }

                if (latestHistory.getOnOff() == OnOff.ON) {
                    // ON → Processor 있음 → eventTime 업데이트
                    latestHistory.updateEventTime(dto.eventTime());
                    ledHistoryRepository.save(latestHistory);
                } else if (latestHistory.getOnOff() == OnOff.OFF) {
                    // OFF → Processor 있음 → ON 이벤트 추가
                    LedHistory onHistory = dto.toLedHistory(OnOff.ON);
                    ledHistoryRepository.save(onHistory);
                }
            } else {
                // DB 없음 → Processor 있음 → ON 이벤트 추가
                LedHistory onHistory = dto.toLedHistory(OnOff.ON);
                ledHistoryRepository.save(onHistory);
            }
        }

        // ON -> Processor에 없음 → OFF 이벤트 추가
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
