package org.farm.fireflyserver.domain.manager.service;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.exception.EntityNotFoundException;
import org.farm.fireflyserver.common.response.ErrorCode;
import org.farm.fireflyserver.domain.care.persistence.entity.Type;
import org.farm.fireflyserver.domain.care.service.CareService;
import org.farm.fireflyserver.domain.manager.persistence.ManagerRepository;
import org.farm.fireflyserver.domain.manager.web.dto.ManagerDto;
import org.farm.fireflyserver.domain.senior.service.SeniorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {
    private final ManagerRepository managerRepository;
    private final SeniorService seniorService;
    private final CareService careService;

    @Override
    public List<ManagerDto.SimpleInfo> getAllManagers() {
        return managerRepository.findManagerSimpleInfoList();
    }

    @Override
    public ManagerDto.DetailInfo getManagerById(Long id) {
        return managerRepository.findDetailInfoById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagerDto.SeniorInfo> getSeniorsByManagerId(Long id) {
        if (!managerRepository.existsById(id)) {
            throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND);
        }

        List<Long> seniorIds = careService.getSeniorIdsByManagerId(id);

        return seniorService.getSeniorInfoByIds(seniorIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagerDto.CareSeniorInfo> getCareSeniorInfoByManagerAndCareType(Long managerId, Type careType) {
        if (!managerRepository.existsById(managerId)) {
            throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND);
        }

        return careService.getCareSeniorInfoByManagerAndCareType(managerId, careType);
    }
}
