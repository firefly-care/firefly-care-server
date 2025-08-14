package org.farm.fireflyserver.senior.domain;

import org.farm.fireflyserver.senior.application.command.RegisterSeniorCommand;

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
    public static Senior from(RegisterSeniorCommand command) {
        return new Senior(
                command.name(),
                command.gender(),
                command.birthday(),
                command.address(),
                command.town(),
                command.phoneNum(),
                command.homePhoneNum(),
                command.zipCode(),
                command.guardianName(),
                command.guardianPhoneNum(),
                command.isHighRisk(),
                command.benefitType(),
                command.memo()
        );
    }
}
