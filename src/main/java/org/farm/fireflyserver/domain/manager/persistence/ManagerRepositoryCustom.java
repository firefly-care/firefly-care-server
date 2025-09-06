package org.farm.fireflyserver.domain.manager.persistence;

import org.farm.fireflyserver.domain.manager.web.dto.ManagerDto;

import java.util.List;

public interface ManagerRepositoryCustom {
    List<ManagerDto.SimpleInfo> findManagerSimpleInfoList();
}
