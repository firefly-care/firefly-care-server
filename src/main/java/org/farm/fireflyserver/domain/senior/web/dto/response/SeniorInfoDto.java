package org.farm.fireflyserver.domain.senior.web.dto.response;

import org.farm.fireflyserver.domain.led.web.dto.response.LedStateDto;
import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;

import java.time.LocalDate;
import java.util.List;

public record SeniorInfoDto(
        String name,
        LocalDate birthday,
        String phoneNum,
        String address,
        String managerName,
        String managerPhoneNum,
        Integer lastActTime,
        String deviceStatus,

        List<LedStateDto> ledStates
) {
    public static  SeniorInfoDto of(Senior senior, Integer lastActTime, String deviceStatus,List<LedStateDto> ledStates) {

        return new SeniorInfoDto(
                senior.getName(),
                senior.getBirthday() ,
                senior.getPhoneNum(),
                senior.getAddress(),
                senior.getManager().getName(),
                senior.getManager().getPhoneNum(),
                lastActTime,
                deviceStatus,
                ledStates
        );
    }
}
