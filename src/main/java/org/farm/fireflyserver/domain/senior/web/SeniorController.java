package org.farm.fireflyserver.domain.senior.web;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.farm.fireflyserver.domain.senior.service.SeniorService;
import org.farm.fireflyserver.domain.senior.web.dto.request.RegisterSeniorDto;
import org.farm.fireflyserver.domain.senior.web.dto.response.SeniorInfoDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/senior")
@RequiredArgsConstructor
public class SeniorController {

    private final SeniorService seniorService;

    //대상자 등록
    @PostMapping
    public BaseResponse<?> registerSenior(@RequestBody RegisterSeniorDto dto) {
        seniorService.registerSenior(dto);
        return BaseResponse.of(SuccessCode.CREATED, null);
    }


    // 대상자 목록 정보 조회
    //TODO : LED 데이터 정보 추가
    @GetMapping
    public BaseResponse<?>getSeniors() {
        List<SeniorInfoDto> seniorInfo = seniorService.getSeniorInfo();
        return BaseResponse.of(SuccessCode.OK, seniorInfo);
    }




}
