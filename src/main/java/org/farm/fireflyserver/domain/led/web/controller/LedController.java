package org.farm.fireflyserver.domain.led.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Led", description = "LED 관련 API")
public class LedController {

    private final LedService ledService;

    //임시로 토큰 없이 저장
    @Operation(summary = "LED 데이터 저장(구현 X)")
            @PostMapping("/save")
    BaseResponse<?> saveLedData(@RequestBody SaveLedDataDto dto){
        ledService.saveLedData(dto);
        return BaseResponse.of(SuccessCode.CREATED, null);
    }

}
