package org.farm.fireflyserver.domain.led.web.dto.request;

import org.farm.fireflyserver.domain.led.persistence.entity.SensorGbn;

import java.sql.Timestamp;

public record SaveLedDataDto(
        SensorGbn sensorGbn,
        Timestamp regDt,
        String ledMtchnSn
) {
}
