package org.farm.fireflyserver.domain.led.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SensorGbn {

    BED_ROOM("01", "안방"),
    LIVING_ROOM("02", "거실"),
    KITCHEN("03", "주방"),
    TOILET("04", "화장실");

    private final String code;
    private final String desc;

    //code->enum
    public static SensorGbn fromCode(String code) {
        if (code != null) {
            for (SensorGbn sensorGbn : SensorGbn.values()) {
                if (sensorGbn.getCode().equals(code)) {
                    return sensorGbn;
                }
            }
            throw new IllegalArgumentException();
        }
        return null;
    }

}
