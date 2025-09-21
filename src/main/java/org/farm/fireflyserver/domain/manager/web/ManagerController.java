package org.farm.fireflyserver.domain.manager.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.farm.fireflyserver.domain.care.persistence.entity.Type;
import org.farm.fireflyserver.domain.manager.service.ManagerService;
import org.farm.fireflyserver.domain.manager.web.dto.ManagerDto;
import org.farm.fireflyserver.domain.manager.web.dto.ManagerNameDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
@Tag(name = "Manager", description = "돌봄 담당자 관련 API")
public class ManagerController {
    private final ManagerService managerService;

    @Operation(summary = "전체 담당자 간략 정보 조회", description = "모든 돌봄 담당자의 간략한 정보를 조회합니다.")
    @GetMapping()
    public BaseResponse<?> getAllManagers() {
        List<ManagerDto.SimpleInfo> dtos = managerService.getAllManagers();

        return BaseResponse.of(SuccessCode.OK, dtos);
    }

    @Operation(summary = "특정 담당자 상세 정보 조회", description = "ID를 사용하여 특정 돌봄 담당자의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public BaseResponse<?> getManagerById(@Parameter(description = "담당자 ID") @PathVariable Long id) {
        ManagerDto.DetailInfo dto = managerService.getManagerById(id);

        return BaseResponse.of(SuccessCode.OK, dto);
    }

    @Operation(summary = "담당자의 담당 대상자 정보 조회", description = "특정 담당자가 담당하는 모든 대상자의 정보를 조회합니다.")
    @GetMapping("/{id}/seniors")
    public BaseResponse<?> getSeniorsByManagerId(@Parameter(description = "담당자 ID") @PathVariable Long id) {
        List<ManagerDto.SeniorInfo> dtos = managerService.getSeniorsByManagerId(id);

        return BaseResponse.of(SuccessCode.OK, dtos);
    }

    @Operation(summary = "담당자의 안부전화 돌봄 정보 조회", description = "특정 담당자의 안부전화 돌봄 대상자 및 최근 돌봄 일시 정보를 조회합니다.")
    @GetMapping("/{id}/call")
    public BaseResponse<?> getCareCallsByManagerId(@Parameter(description = "담당자 ID") @PathVariable Long id) {
        List<ManagerDto.CareSeniorInfo> dtos = managerService.getCareSeniorInfoByManagerAndCareType(id, Type.CALL);

        return BaseResponse.of(SuccessCode.OK, dtos);
    }

    @Operation(summary = "담당자의 방문 돌봄 정보 조회", description = "특정 담당자의 방문 돌봄 대상자 및 최근 돌봄 일시 정보를 조회합니다.")
    @GetMapping("/{id}/visit")
    public BaseResponse<?> getCareVisitsByManagerId(@Parameter(description = "담당자 ID") @PathVariable Long id) {
        List<ManagerDto.CareSeniorInfo> dtos = managerService.getCareSeniorInfoByManagerAndCareType(id, Type.VISIT);

        return BaseResponse.of(SuccessCode.OK, dtos);
    }

    //담당자 {이름,전화번호} 리스트 조회
    @Operation(summary = "대상자 등록시 돌봄 담당자 {이름,전화번호} 리스트 조회", description = "대상자 등록시 `1.돌봄 담당자 이름 리스트 조회` -> `2.해당 이름 클릭시 전화번호 조회`에 사용")
    @GetMapping("/name")
    public BaseResponse<?> getManagerNames() {
        List<ManagerNameDto> managerNameList = managerService.getManagerNameList();
        return BaseResponse.of(SuccessCode.OK, managerNameList);
    }

}