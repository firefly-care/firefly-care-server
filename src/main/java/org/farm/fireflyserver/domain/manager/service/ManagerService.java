package org.farm.fireflyserver.domain.manager.service;

import org.farm.fireflyserver.domain.manager.web.dto.ManagerDto;

import java.util.List;

public interface ManagerService {
    public List<ManagerDto.SimpleInfo> getAllManagers();
    public ManagerDto.DetailInfo getManagerById(Long id);
    public List<ManagerDto.SeniorInfo> getSeniorsByManagerId(Long id);
}
