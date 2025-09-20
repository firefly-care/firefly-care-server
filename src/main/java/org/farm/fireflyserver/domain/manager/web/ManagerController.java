package org.farm.fireflyserver.domain.manager.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.farm.fireflyserver.domain.care.persistence.entity.Type;
import org.farm.fireflyserver.domain.manager.service.ManagerService;
import org.farm.fireflyserver.domain.manager.web.dto.ManagerDto;
import org.farm.fireflyserver.domain.manager.web.dto.ManagerNameDto;
import org.farm.fireflyserver.domain.manager.web.dto.ManagerRegisterDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
@Tag(name = "Manager", description = "돌봄 담당자 관련 API")
public class ManagerController {
    private final ManagerService managerService;

    @GetMapping()
    public BaseResponse<?> getAllManagers() {
        List<ManagerDto.SimpleInfo> dtos = managerService.getAllManagers();

        return BaseResponse.of(SuccessCode.OK, dtos);
    }

    @GetMapping("/{id}")
    public BaseResponse<?> getManagerById(@PathVariable Long id) {
        ManagerDto.DetailInfo dto = managerService.getManagerById(id);

        return BaseResponse.of(SuccessCode.OK, dto);
    }

    @GetMapping("/{id}/seniors")
    public BaseResponse<?> getSeniorsByManagerId(@PathVariable Long id) {
        List<ManagerDto.SeniorInfo> dtos = managerService.getSeniorsByManagerId(id);

        return BaseResponse.of(SuccessCode.OK, dtos);
    }

    @GetMapping("/{id}/call")
    public BaseResponse<?> getCareCallsByManagerId(@PathVariable Long id) {
        List<ManagerDto.CareSeniorInfo> dtos = managerService.getCareSeniorInfoByManagerAndCareType(id, Type.CALL);

        return BaseResponse.of(SuccessCode.OK, dtos);
    }

    @GetMapping("/{id}/visit")
    public BaseResponse<?> getCareVisitsByManagerId(@PathVariable Long id) {
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

    //담당자 등록
    @PostMapping
    public BaseResponse<?> registerManager(@RequestBody ManagerRegisterDto dto) {
        managerService.registerManager(dto);
        return BaseResponse.of(SuccessCode.OK);
    }


}