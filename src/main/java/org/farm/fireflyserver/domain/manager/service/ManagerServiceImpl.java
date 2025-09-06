package org.farm.fireflyserver.domain.manager.service;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.exception.EntityNotFoundException;
import org.farm.fireflyserver.common.response.ErrorCode;
import org.farm.fireflyserver.domain.manager.persistence.ManagerRepository;
import org.farm.fireflyserver.domain.manager.web.dto.ManagerDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {
    private final ManagerRepository managerRepository;

    @Override
    public List<ManagerDto.SimpleInfo> getAllManagers() {
        return managerRepository.findManagerSimpleInfoList();
    }

    @Override
    public ManagerDto.DetailInfo getManagerById(Long id) {
        return managerRepository.findDetailInfoById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));
    }
}
