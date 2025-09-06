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
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

// 최신 ON 이벤트를 LED 히스토리에 저장 & 업데이트하는 Writer
@RequiredArgsConstructor
public class LedItemWriter implements ItemWriter<Map<String, LedDataLogDto>> {

    private final LedHistoryRepository ledHistoryRepository;
    private final Map<String, LocalDateTime> windowTimes;
    private final Map<String, LocalDateTime> lastWritten = new HashMap<>();

    @Override
    public void write(Chunk<? extends Map<String, LedDataLogDto>> chunk ) {

        Map<String, LedDataLogDto> latestMap = new HashMap<>();

        final LocalDateTime start = windowTimes.get("windowStart");
        final LocalDateTime now = windowTimes.get("now");

        // 청크 단위로 Processor에서 온 최신 ON 로그 병합
        for (Map<String, LedDataLogDto> map : chunk) {
            for (Map.Entry<String, LedDataLogDto> entry : map.entrySet()) {
                String key = entry.getKey();
                LedDataLogDto dto = entry.getValue();

                //임시 : 시간 재검증용
                if (dto.eventTime().isBefore(start) || !dto.eventTime().isBefore(now)) {
                    continue;
                }

                LedDataLogDto existing = latestMap.get(key);
                if (existing == null || existing.eventTime().isBefore(dto.eventTime())) {
                    latestMap.put(key, dto);
                }
            }
        }

        // ON 이벤트 처리 결과 저장용
        List<LedHistory> toSave = getSaveList(latestMap);

        if (!toSave.isEmpty()) {
            ledHistoryRepository.saveAll(toSave);
        }
    }

    private List<LedHistory> getSaveList(Map<String, LedDataLogDto> latestMap) {
        List<LedHistory> toSave = new ArrayList<>();

        for (Map.Entry<String, LedDataLogDto> entry : latestMap.entrySet()) {
            String key = entry.getKey();
            LedDataLogDto dto = entry.getValue();

            LocalDateTime prev = lastWritten.get(key);
            if (prev != null && !dto.eventTime().isAfter(prev)) {
                continue;
            }

            String[] parts = key.split("_", 2);
            String ledMtchnSn = parts[0];
            SensorGbn sensorGbn = SensorGbn.fromCode(parts[1]);

            LedHistory candidate = decideUpsert(ledMtchnSn, sensorGbn, dto);
            if (candidate != null) {
                toSave.add(candidate);
                lastWritten.put(key, dto.eventTime());
            }
        }

        return toSave;
    }


    private LedHistory decideUpsert(String ledMtchnSn, SensorGbn sensorGbn, LedDataLogDto dto) {
        // 해당 LED센서의 최신 히스토리 조회
        LedHistory latestHistory = ledHistoryRepository
                .findTopByLedMtchnSnAndSensorGbnOrderByEventTimeDescLedHistoryIdDesc(ledMtchnSn, sensorGbn);

        // CASE 1: DB 없음 → Processor 있음 → ON 이벤트 추가
        if (latestHistory == null) {
            if (!ledHistoryRepository.existsByLedMtchnSnAndSensorGbnAndOnOffAndEventTime(
                    ledMtchnSn, sensorGbn, OnOff.ON, dto.eventTime())) {
                return dto.toLedHistory(OnOff.ON);
            }
            return null;
        }
        // CASE 2: DB 있음(OFF) → Processor 있음 → ON 이벤트 추가
        else if (latestHistory.getOnOff() == OnOff.OFF) {
            if (!ledHistoryRepository.existsByLedMtchnSnAndSensorGbnAndOnOffAndEventTime(
                    ledMtchnSn, sensorGbn, OnOff.ON, dto.eventTime())) {
                return dto.toLedHistory(OnOff.ON);
            }
            return null;
        }
        // CASE 3: DB 있음(ON) → Processor 있음 → eventTime 시간 업데이트
        else if (latestHistory.getOnOff() == OnOff.ON &&
                !latestHistory.getEventTime().isEqual(dto.eventTime())) {
            latestHistory.updateEventTime(dto.eventTime());
            return latestHistory;
        }

        return null;
    }
}
