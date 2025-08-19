package org.farm.fireflyserver.domain.senior.service;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.senior.web.mapper.SeniorMapper;
import org.farm.fireflyserver.domain.senior.web.dto.request.RegisterSeniorDto;
import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;
import org.farm.fireflyserver.domain.senior.persistence.repository.SeniorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeniorServiceImpl implements SeniorService {

    private final SeniorRepository seniorRepository;
    private final SeniorMapper seniorMapper;

    @Transactional
    public void registerSenior(RegisterSeniorDto dto) {
        Senior senior = seniorMapper.toEntity(dto);
        seniorRepository.save(senior);
    }

}
