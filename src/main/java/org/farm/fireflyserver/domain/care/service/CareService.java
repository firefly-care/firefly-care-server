package org.farm.fireflyserver.domain.care.service;

import org.farm.fireflyserver.domain.care.web.dto.CareDTO;

import java.util.List;

public interface CareService {
    void addCare(CareDTO.Register dto);
    List<CareDTO.Response> getAllCare();
}