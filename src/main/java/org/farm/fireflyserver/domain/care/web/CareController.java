package org.farm.fireflyserver.domain.care.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.farm.fireflyserver.domain.care.service.CareService;
import org.farm.fireflyserver.domain.care.web.dto.CareDto;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@Tag(name = "Care", description = "돌봄 관련 api")
@RestController
@RequestMapping("/care")
@RequiredArgsConstructor
public class CareController {
    private final CareService service;

    @Operation(summary = "돌봄 현황 페이지(리스트형) - 전체 돌봄 현황 조회", description = "전체 돌봄 정보를 조회합니다.")
    @GetMapping()
    public BaseResponse<?> getAllCare() {
        List<CareDto.Response> dto = service.getAllCare();

        return BaseResponse.of(SuccessCode.OK, dto);
    }

    @Operation(summary = "돌봄 정보 페이지 - 돌봄 추가", description = "새로운 돌봄 정보를 추가합니다.")
    @PostMapping("/add")
    public BaseResponse<?> addCare(@RequestBody CareDto.Register dto) {
        service.addCare(dto);

        return BaseResponse.of(SuccessCode.OK);
    }

    @Operation(summary = "돌봄 현황 페이지(리스트형) - 돌봄 검색", description = "조건에 맞는 돌봄 정보를 검색합니다.")
    @GetMapping("/search")
    public BaseResponse<?> searchCare(@ModelAttribute CareDto.SearchRequest dto) {
        List<CareDto.Response> response = service.searchCare(dto);

        return BaseResponse.of(SuccessCode.OK, response);
    }

    @Operation(summary = "대상자 관리 상세 페이지 - 대상자 월별 돌봄 현황 조회", description = "특정 대상자의 월별 돌봄 현황을 조회합니다.")
    @GetMapping("/senior-monthly-status")
    public BaseResponse<?> seniorMonthlyStatus(@Parameter(description = "대상자 ID") Long seniorId, @Parameter(description = "조회 년월") YearMonth yearMonth) {
        CareDto.MonthlyCare dto = service.getSeniorMonthlyCare(seniorId, yearMonth);

        return BaseResponse.of(SuccessCode.OK, dto);
    }
}