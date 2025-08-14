package org.farm.fireflyserver.senior.application.command;

import org.farm.fireflyserver.common.util.CommandUtil;
import org.farm.fireflyserver.senior.adapter.in.web.dto.request.RegisterSeniorDto;
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

    public static RegisterSeniorCommand from(RegisterSeniorDto dto){
        return new RegisterSeniorCommand(
                dto.name(),
                dto.gender(),
                dto.birthday(),
                dto.address(),
                dto.town(),
                dto.phoneNum(),
                dto.homePhoneNum(),
                dto.zipCode(),
                dto.guardianName(),
                dto.guardianPhoneNum(),
                dto.isHighRisk(),
                dto.benefitType(),
                dto.memo()
        );
    }
}
