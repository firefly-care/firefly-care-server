package org.farm.fireflyserver.domain.senior.service;

import org.farm.fireflyserver.domain.senior.web.dto.request.RegisterSeniorDto;
import org.farm.fireflyserver.domain.senior.web.dto.request.RequestSeniorDto;
import org.farm.fireflyserver.domain.senior.web.dto.response.SeniorDetailDto;
import org.farm.fireflyserver.domain.senior.web.dto.response.SeniorInfoDto;

import java.util.List;

public interface SeniorService {
    void registerSenior(RegisterSeniorDto dto);
    List<SeniorInfoDto> getSeniorInfo();

    List<SeniorInfoDto> searchSeniors( Boolean isActive, String keywordType, String keyword);

    SeniorDetailDto getSeniorDetail(Long seniorId);

    void deactivateSenior(RequestSeniorDto.Deactivate dto);
}