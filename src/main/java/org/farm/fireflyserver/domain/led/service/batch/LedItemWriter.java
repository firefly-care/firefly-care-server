package org.farm.fireflyserver.domain.led.service.batch;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;
import org.farm.fireflyserver.domain.led.persistence.entity.OnOff;
import org.farm.fireflyserver.domain.led.persistence.entity.SensorGbn;
import org.farm.fireflyserver.domain.led.persistence.repository.LedHistoryRepository;
import org.farm.fireflyserver.domain.led.web.dto.response.LedDataLogDto;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.HashMap;
import java.util.Map;

// 최신 ON 이벤트를 LED 히스토리에 저장 & 업데이트하는 Writer
@RequiredArgsConstructor
public class LedItemWriter implements ItemWriter<Map<String, LedDataLogDto>> {

    private final LedHistoryRepository ledHistoryRepository;

    @Override
    public void write(Chunk<? extends Map<String, LedDataLogDto>> chunk) throws Exception {

        Map<String, LedDataLogDto> latestMap = new HashMap<>();

        // 청크 단위로 Processor에서 온 최신 ON 로그 병합
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

        // ON 이벤트 처리
        for (Map.Entry<String, LedDataLogDto> entry : latestMap.entrySet()) {
            String key = entry.getKey();
            LedDataLogDto dto = entry.getValue();
            String[] parts = key.split("_");

            String ledMtchnSn = parts[0];
            SensorGbn sensorGbn = SensorGbn.fromCode(parts[1]);

            // 해당 LED센서의 최신 히스토리 조회
            LedHistory latestHistory = ledHistoryRepository
                    .findTopByLedMtchnSnAndSensorGbnOrderByEventTimeDesc(ledMtchnSn, sensorGbn);

            // CASE 1: DB 없음 → Processor 있음 → ON 이벤트 추가
            if (latestHistory == null) {
                ledHistoryRepository.save(dto.toLedHistory(OnOff.ON));
            }
            // CASE 2: DB 있음(OFF) → Processor 있음 → ON 이벤트 추가
            else if (latestHistory.getOnOff() == OnOff.OFF) {
                ledHistoryRepository.save(dto.toLedHistory(OnOff.ON));
            }
            // CASE 3: DB 있음(ON) → Processor 있음 → eventTime 시간 업데이트
            else if (latestHistory.getOnOff() == OnOff.ON &&
                    !latestHistory.getEventTime().isEqual(dto.eventTime())) {
                latestHistory.updateEventTime(dto.eventTime());
                ledHistoryRepository.save(latestHistory);
            }
        }
    }
}
