package org.farm.fireflyserver.domain.led.web.dto.response;

import org.farm.fireflyserver.domain.led.persistence.entity.LedState;
import org.farm.fireflyserver.domain.led.persistence.entity.OnOff;

public record LedStateDto(
        String sensorGbn,
        boolean isOn
) {

    public static LedStateDto of(LedState ledState) {
        return new LedStateDto(
                ledState.getSensorGbn().getDesc(),
                ledState.getOnOff() == OnOff.ON
        );
    }
}
