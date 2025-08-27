package org.farm.fireflyserver.domain.led.web.dto.response;

import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;
import org.farm.fireflyserver.domain.led.persistence.entity.OnOff;
import org.farm.fireflyserver.domain.led.persistence.entity.SensorGbn;

import java.time.LocalDateTime;

public record LedDataLogDto(
      String ledMtchnSn,
      SensorGbn sensorGbn,
      LocalDateTime eventTime,
      OnOff onOff
) {
    public LedHistory toLedHistory(OnOff onOff) {
        return LedHistory.builder()
                .ledMtchnSn(this.ledMtchnSn)
                .sensorGbn(this.sensorGbn)
                .onOff(onOff)
                .eventTime(this.eventTime)
                .build();
    }
}
