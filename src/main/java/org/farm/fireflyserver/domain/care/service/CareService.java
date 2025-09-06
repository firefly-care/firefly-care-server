package org.farm.fireflyserver.domain.care.service;

import org.farm.fireflyserver.domain.care.web.dto.CareDto;

import java.time.YearMonth;
import java.util.List;

public interface CareService {
    void addCare(CareDto.Register dto);
    List<CareDto.Response> getAllCare();
    List<CareDto.Response> searchCare(CareDto.SearchRequest request);
    CareDto.MonthlyCare getSeniorMonthlyCare(Long seniorId, YearMonth yearMonth);
    List<Long> getSeniorIdsByManagerId(Long managerId);
}