package org.farm.fireflyserver.domain.led.service.batch;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.led.web.dto.response.LedDataLogDto;
import org.springframework.batch.item.ItemProcessor;

import java.util.HashMap;
import java.util.Map;

//  각 LED별로 가장 최신 로그만 남기는 Processor
@RequiredArgsConstructor
public class LedItemProcessor implements ItemProcessor<LedDataLogDto, Map<String, LedDataLogDto>> {

    private final Map<String, LedDataLogDto> latestMap;

    @Override
    public Map<String, LedDataLogDto> process(LedDataLogDto item) {
        // LED 식별자 + 센서구분(위치)별로 가장 최신 로그만 저장
        String key = item.ledMtchnSn() + "_" + item.sensorGbn().getCode();

        LedDataLogDto existing = latestMap.get(key);
        if (existing == null || existing.eventTime().isBefore(item.eventTime())) {
            latestMap.put(key, item);
        }

        return latestMap;
    }
}

