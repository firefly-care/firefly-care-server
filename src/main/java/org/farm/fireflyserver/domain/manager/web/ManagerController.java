package org.farm.fireflyserver.domain.manager.web;

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

    //담당자 {이름,전화번호} 리스트
    @GetMapping("/name")
    public BaseResponse<?> getManagerNames() {
        List<ManagerNameDto> managerNameList = managerService.getManagerNameList();
        return BaseResponse.of(SuccessCode.OK, managerNameList);
    }

}