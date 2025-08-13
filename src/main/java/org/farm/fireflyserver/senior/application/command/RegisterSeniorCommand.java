package org.farm.fireflyserver.senior.application.command;

import org.farm.fireflyserver.common.util.CommandUtil;
import org.farm.fireflyserver.senior.domain.BenefitType;
import org.farm.fireflyserver.senior.domain.Gender;

import java.time.LocalDate;

public record RegisterSeniorCommand(
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
    public RegisterSeniorCommand {
        CommandUtil.throwIfNull(name, "이름은 필수 값입니다.");
    }
}
