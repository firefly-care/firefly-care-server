package org.farm.fireflyserver.senior.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.farm.fireflyserver.senior.adapter.in.web.dto.request.RegisterSeniorDto;
import org.farm.fireflyserver.senior.application.command.RegisterSeniorCommand;
import org.farm.fireflyserver.senior.application.port.in.RegisterSeniorUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/senior")
@RequiredArgsConstructor
public class SeniorController {

    private final RegisterSeniorUseCase registerSeniorUseCase;

    @PostMapping("/register")
    public BaseResponse<?> registerSenior(@RequestBody RegisterSeniorDto dto) {

        RegisterSeniorCommand registerSeniorCommand = new RegisterSeniorCommand(
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
        registerSeniorUseCase.registerSenior(registerSeniorCommand);
        return BaseResponse.of(SuccessCode.CREATED, null);
    }
}
