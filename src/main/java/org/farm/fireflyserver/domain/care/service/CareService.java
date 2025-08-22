package org.farm.fireflyserver.domain.care.service;

import org.farm.fireflyserver.domain.care.web.dto.CareDto;

import java.util.List;

public interface CareService {
    void addCare(CareDto.Register dto);
    List<CareDto.Response> getAllCare();
    List<CareDto.Response> searchCare(CareDto.SearchRequest request);
}