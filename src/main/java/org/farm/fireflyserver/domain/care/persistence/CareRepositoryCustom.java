package org.farm.fireflyserver.domain.care.persistence;

import org.farm.fireflyserver.domain.care.persistence.entity.Type;
import org.farm.fireflyserver.domain.manager.web.dto.ManagerDto;

import java.util.List;

public interface CareRepositoryCustom {
    List<Long> findDistinctSeniorIdsByManagerId(Long managerId);
    List<ManagerDto.CareSeniorInfo> getCareSeniorInfoByManagerAndCareType(Long managerId, Type careType);
}
