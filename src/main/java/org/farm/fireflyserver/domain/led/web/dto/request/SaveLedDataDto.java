package org.farm.fireflyserver.domain.led.web.dto.request;

import org.farm.fireflyserver.domain.led.persistence.entity.LedData;
import org.farm.fireflyserver.domain.led.persistence.entity.SensorGbn;


public record SaveLedDataDto(
        SensorGbn sensorGbn,
        String trgSn,
        String snsrSn
) {
    public static LedData toEntity(SaveLedDataDto dto) {
        return LedData.builder()
                .trgSn(dto.trgSn())
                .snsrSn(dto.snsrSn())
                .build();
    }
}
