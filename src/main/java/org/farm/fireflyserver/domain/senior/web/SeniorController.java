package org.farm.fireflyserver.domain.senior.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.farm.fireflyserver.domain.senior.service.SeniorService;
import org.farm.fireflyserver.domain.senior.web.dto.request.RegisterSeniorDto;
import org.farm.fireflyserver.domain.senior.web.dto.request.RequestSeniorDto;
import org.farm.fireflyserver.domain.senior.web.dto.response.SeniorDetailDto;
import org.farm.fireflyserver.domain.senior.web.dto.response.SeniorInfoDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/senior")
@RequiredArgsConstructor
@Tag(name = "Senior", description = "대상자 관련 AP" +
        "I")
public class SeniorController {

    private final SeniorService seniorService;

    // 대상자 등록
    @Operation(summary = "대상자 등록", description = "신규 대상자 등록")
    @PostMapping
    public BaseResponse<?> registerSenior(@RequestBody RegisterSeniorDto dto) {
        seniorService.registerSenior(dto);
        return BaseResponse.of(SuccessCode.CREATED, null);
    }

    // 대상자 목록 조회
    @Operation(summary = "대상자 관리 페이지 - 대상자 목록 조회", description = "전체 대상자 정보 조회")
    @GetMapping
    public BaseResponse<?>getSeniors() {
        List<SeniorInfoDto> seniorInfo = seniorService.getSeniorInfo();
        return BaseResponse.of(SuccessCode.OK, seniorInfo);
    }

    // 대상자 검색
    @Operation(summary = "대상자 관리 페이지 - 대상자 검색",
            description = "서비스 진행 여부 및 키워드 조건으로 대상자를 검색(현재는 문자열인 값들의 검색 지원하는데 수정 가능)")
    @GetMapping("/search")
    public BaseResponse<?> searchSeniors(
            @Parameter(description = "서비스 진행 여부") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "검색 타입 : name(이름), phone(연락처), address(주소), managerName(담당자), magagerPhone(담당자 연락처)")
            @RequestParam(required = false) String keywordType,
            @Parameter(description = "검색 키워드") @RequestParam(required = false) String keyword
           ) {
        List<SeniorInfoDto> seniorInfo = seniorService.searchSeniors(isActive,keywordType,keyword);
        return BaseResponse.of(SuccessCode.OK, seniorInfo);
    }

    @Operation(summary = "대상자 상세 정보 조회", description = "특정 대상자의 상세 정보를 조회")
    @GetMapping("/{seniorId}")
    public BaseResponse<SeniorDetailDto> getSeniorDetail(@Parameter(description = "대상자 식별자") @PathVariable Long seniorId) {
        SeniorDetailDto seniorDetail = seniorService.getSeniorDetail(seniorId);
        return BaseResponse.of(SuccessCode.OK, seniorDetail);
    }

    @Operation(summary = "대상자 서비스 취소", description = "대상자의 돌봄 서비스를 취소")
    @PostMapping("/deactivate")
    public BaseResponse<?> deactivateSenior(@Parameter(description = "대상자 식별자") @RequestBody RequestSeniorDto.Deactivate dto) {
        seniorService.deactivateSenior(dto);

        return BaseResponse.of(SuccessCode.OK);
    }
}