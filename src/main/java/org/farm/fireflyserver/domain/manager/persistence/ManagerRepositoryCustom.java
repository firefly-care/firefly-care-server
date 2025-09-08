package org.farm.fireflyserver.domain.manager.persistence;

import org.farm.fireflyserver.domain.manager.web.dto.ManagerDto;

import java.util.List;
import java.util.Optional;

public interface ManagerRepositoryCustom {
    List<ManagerDto.SimpleInfo> findManagerSimpleInfoList();
    Optional<ManagerDto.DetailInfo> findDetailInfoById(Long id);
}
