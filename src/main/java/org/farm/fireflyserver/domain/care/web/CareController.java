package org.farm.fireflyserver.domain.care.web;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.farm.fireflyserver.domain.care.service.AddCareService;
import org.farm.fireflyserver.domain.care.web.dto.CareDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/care")
@RequiredArgsConstructor
public class CareController {
    private final AddCareService service;

    @GetMapping()
    public BaseResponse<?> getAllCare() {
        List<CareDTO.Response> dto = service.getAllCare();

        return BaseResponse.of(SuccessCode.OK, dto);
    }

    @PostMapping("/add")
    public BaseResponse<?> addCare(@RequestBody CareDTO.Register dto) {
        service.addCare(dto);

        return BaseResponse.of(SuccessCode.OK);
    }
}
