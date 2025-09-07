package org.farm.fireflyserver.domain.manager.service;

import org.farm.fireflyserver.domain.care.persistence.entity.Type;
import org.farm.fireflyserver.domain.manager.web.dto.ManagerDto;

import java.util.List;

public interface ManagerService {
    List<ManagerDto.SimpleInfo> getAllManagers();
    ManagerDto.DetailInfo getManagerById(Long id);
    List<ManagerDto.SeniorInfo> getSeniorsByManagerId(Long id);
    List<ManagerDto.CareSeniorInfo> getCareSeniorInfoByManagerAndCareType(Long managerId, Type careType);
}
