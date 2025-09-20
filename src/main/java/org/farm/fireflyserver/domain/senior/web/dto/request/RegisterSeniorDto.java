package org.farm.fireflyserver.domain.senior.web.dto.request;

import org.farm.fireflyserver.domain.senior.persistence.entity.BenefitType;
import org.farm.fireflyserver.domain.senior.persistence.entity.Gender;

import java.time.LocalDate;

public record RegisterSeniorDto(
        String name,
        Gender gender,
        LocalDate birthday,
        BenefitType benefitType,   //보장 유형
        String managerName,
        String address,
        String town,
        String phoneNum,
        String homePhoneNum,
        String zipCode,
        String guardianName,
        String guardianPhoneNum,
        Boolean isHighRisk,

        String memo
) {

}
