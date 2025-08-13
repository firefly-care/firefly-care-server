package org.farm.fireflyserver.senior.adapter.in.web.dto.request;

import org.farm.fireflyserver.senior.domain.BenefitType;
import org.farm.fireflyserver.senior.domain.Gender;

import java.time.LocalDate;

public record RegisterSeniorDto(
        String name,
        Gender gender,
        LocalDate birthday,
        String address,
        String town,
        String phoneNum,
        String homePhoneNum,
        String zipCode,
        String guardianName,
        String guardianPhoneNum,
        Boolean isHighRisk,
        BenefitType benefitType,
        String memo
) {
}
