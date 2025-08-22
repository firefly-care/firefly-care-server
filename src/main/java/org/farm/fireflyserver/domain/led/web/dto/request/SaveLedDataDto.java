package org.farm.fireflyserver.domain.led.web.dto.request;

import org.farm.fireflyserver.domain.led.persistence.entity.SensorGbn;

public record SaveLedDataDto(
        SensorGbn sensorGbn,
        String trgSn,
        String snsrSn
) {
}
