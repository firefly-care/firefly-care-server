package org.farm.fireflyserver.domain.manager.web.dto;

import org.farm.fireflyserver.domain.senior.persistence.entity.Gender;

import java.time.LocalDate;

public record ManagerRegisterDto(
        Long accountId,
        String managerName,
        Gender gender,
        LocalDate birthday,
        String affiliation,
        String zipCode,
        String address,
        String phoneNum,
        String subPhoneNum,
        String note,
        String imageUrl
) {
}
