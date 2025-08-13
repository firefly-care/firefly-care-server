package org.farm.fireflyserver.senior.domain;

import java.time.LocalDate;

public record Senior(
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
