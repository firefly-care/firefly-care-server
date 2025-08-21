package org.farm.fireflyserver.domain.senior.web;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.farm.fireflyserver.domain.senior.service.SeniorService;
import org.farm.fireflyserver.domain.senior.web.dto.request.RegisterSeniorDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/senior")
@RequiredArgsConstructor
public class SeniorController {

    private final SeniorService seniorService;

    @PostMapping("/register")
    public BaseResponse<?> registerSenior(@RequestBody RegisterSeniorDto dto) {
        seniorService.registerSenior(dto);
        return BaseResponse.of(SuccessCode.CREATED, null);
    }

//    @GetMapping("/")
//    public BaseResponse<?> findSenior() {
//
//    }
}
