package org.farm.fireflyserver.domain.senior.web.dto.response;

import org.farm.fireflyserver.domain.led.web.dto.response.LedStateDto;
import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;

import java.time.LocalDate;
import java.util.List;

public record SeniorInfoDto(
        String name,
        LocalDate birthday,
        String phoneNum,
        String town,
        String address,
        String managerName,
        String managerPhoneNum,
        SeniorStateDto seniorState,
        List<LedStateDto> ledStates
) {
    public static  SeniorInfoDto of(Senior senior, String managerName, String managerPhoneNum, SeniorStateDto seniorState,List<LedStateDto>  ledStates) {
        return new SeniorInfoDto(
                senior.getName(),
                senior.getBirthday() ,
                senior.getPhoneNum(),
                senior.getTown(),
                senior.getAddress(),
                managerName,
                managerPhoneNum,
                seniorState,
                ledStates
        );

    }
}
