package org.farm.fireflyserver.domain.led.service.batch;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;
import org.farm.fireflyserver.domain.led.persistence.entity.LedState;
import org.farm.fireflyserver.domain.led.persistence.entity.OnOff;
import org.farm.fireflyserver.domain.led.persistence.entity.SensorGbn;
import org.farm.fireflyserver.domain.led.persistence.repository.LedHistoryRepository;
import org.farm.fireflyserver.domain.led.persistence.repository.LedStateRepository;
import org.farm.fireflyserver.domain.led.web.dto.response.LedDataLogDto;
import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;
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
    private final LedStateRepository ledStateRepository;

    private final Map<String, LocalDateTime> windowTimes;
    private final Map<String, LocalDateTime> lastWritten = new HashMap<>();

    @Override
    public void write(Chunk<? extends Map<String, LedDataLogDto>> chunk) {
        final LocalDateTime start = windowTimes.get("windowStart");
        final LocalDateTime now = windowTimes.get("now");

        // 청크 단위로 Processor에서 온 최신 ON 로그 가장 최근 1건으로 병합
        Map<String, LedDataLogDto> latestMap = new HashMap<>();
        for (Map<String, LedDataLogDto> map : chunk) {
            for (Map.Entry<String, LedDataLogDto> e : map.entrySet()) {
                LedDataLogDto dto = e.getValue();
                if (dto.eventTime().isBefore(start) || !dto.eventTime().isBefore(now)) continue; // 윈도우 검증

                latestMap.merge(
                        e.getKey(),
                        dto,
                        (oldV, newV) -> oldV.eventTime().isBefore(newV.eventTime()) ? newV : oldV
                );
            }
        }

        List<LedHistory> toSave = getSaveList(latestMap);
        if (!toSave.isEmpty()) {
            ledHistoryRepository.saveAll(toSave);
        }
    }


    private List<LedHistory> getSaveList(Map<String, LedDataLogDto> latestMap) {
        List<LedHistory> toSave = new ArrayList<>();

        for (Map.Entry<String, LedDataLogDto> e : latestMap.entrySet()) {
            String key = e.getKey();
            LedDataLogDto dto = e.getValue();

            LocalDateTime prev = lastWritten.get(key);
            if (prev != null && !dto.eventTime().isAfter(prev)) continue; // 중복 방지

            String[] parts = key.split("_", 2);
            String ledMtchnSn = parts[0];
            SensorGbn sensorGbn = SensorGbn.fromCode(parts[1]);

            LedHistory candidate = upsertStateAndHistory(ledMtchnSn, sensorGbn, dto);
            if (candidate != null) {
                toSave.add(candidate);
                lastWritten.put(key, dto.eventTime());
            }
        }
        return toSave;
    }

    // LedState 생성/갱신 + LedHistory 생성 결정 함수
    private LedHistory upsertStateAndHistory(String ledMtchnSn, SensorGbn sensorGbn, LedDataLogDto dto) {
        final LocalDateTime now = windowTimes.get("now");
        LedHistory historyToSave = null;

        // 현재 상태 조회
        LedState state = ledStateRepository
                .findByLedMtchnSnAndSensorGbn(ledMtchnSn, sensorGbn)
                .orElse(null);

        //CASE 1 : 상태 없음 → 새로 생성(ON) + 최초 ON 히스토리 기록
        if (state == null) {
            state = LedState.builder()
                    .ledMtchnSn(ledMtchnSn)
                    .sensorGbn(sensorGbn)
                    .onOff(OnOff.ON)
                    .senior(Senior.builder().seniorId(1L).build()) // TODO: 임시 매핑. 실제 senior 매핑으로 수정
                    .build();
            ledStateRepository.save(state);

            // LedHistory 기록
            if (!ledHistoryRepository.existsByLedMtchnSnAndSensorGbnAndOnOffAndEventTime(
                    ledMtchnSn, sensorGbn, OnOff.ON, dto.eventTime())) {
                historyToSave = dto.toLedHistory(OnOff.ON);
            }
        }
        //CASE 2 : LedState=OFF → ON으로 전환 + ON 히스토리 기록
        else if (state.getOnOff() == null || state.getOnOff() == OnOff.OFF) {
            boolean exists = ledHistoryRepository.existsByLedMtchnSnAndSensorGbnAndOnOffAndEventTime(
                    ledMtchnSn, sensorGbn, OnOff.ON, dto.eventTime());
            if (!exists) {
                state.updateState(OnOff.ON);
                ledStateRepository.save(state);
                historyToSave = dto.toLedHistory(OnOff.ON);
            }
        }
        //CASE 3 : LedState=ON → 상태는 유지하되 updatedAt만 갱신 (히스토리 추가 없음)
        else {
            state.updateTime(now);
            ledStateRepository.save(state);
        }

        return historyToSave;
    }
}
