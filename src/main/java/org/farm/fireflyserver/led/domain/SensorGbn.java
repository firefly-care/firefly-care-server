package org.farm.fireflyserver.led.domain;

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

    //코드로 저장시 Convert 필요

}
