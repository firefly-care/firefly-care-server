package org.farm.fireflyserver.domain.care.web;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.farm.fireflyserver.domain.care.service.AddCareService;
import org.farm.fireflyserver.domain.care.web.dto.AddCareDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/care")
@RequiredArgsConstructor
public class CareController {
    private final AddCareService service;

    @PostMapping("/add")
    public BaseResponse<?> addCare(@RequestBody AddCareDto dto) {
        service.addCare(dto);

        return BaseResponse.of(SuccessCode.OK);
    }
}
