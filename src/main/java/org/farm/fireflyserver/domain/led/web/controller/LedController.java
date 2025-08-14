package org.farm.fireflyserver.domain.led.web.controller;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.farm.fireflyserver.domain.led.web.dto.request.SaveLedDataDto;
import org.farm.fireflyserver.domain.led.service.LedService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/led")
public class LedController {

    private final LedService ledService;

    //임시로 토큰 없이 저장
    @PostMapping("/save")
    BaseResponse<?> saveLedData(@RequestBody SaveLedDataDto dto){
        ledService.saveLedData(dto);
        return BaseResponse.of(SuccessCode.CREATED, null);
    }

}
