package org.farm.fireflyserver.domain.senior.service;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.care.persistence.entity.Care;
import org.farm.fireflyserver.domain.senior.web.dto.response.SeniorInfoDto;
import org.farm.fireflyserver.domain.senior.web.dto.response.SeniorStateDto;
import org.farm.fireflyserver.domain.senior.web.mapper.SeniorMapper;
import org.farm.fireflyserver.domain.senior.web.dto.request.RegisterSeniorDto;
import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;
import org.farm.fireflyserver.domain.senior.persistence.repository.SeniorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

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

    @Override
    public List<SeniorInfoDto> getSeniorInfo() {
        List<Senior> seniors = seniorRepository.findAll();

        return seniors.stream()
                .map(senior -> {

                    // 가장 최근 돌봄 이력
                    Care latestCare = senior.getCareList().stream()
                            .max(Comparator.comparing(Care::getDate))
                            .orElse(null);

                    String managerName = null;
                    String managerPhone = null;
                    SeniorStateDto seniorState = null;

                    if (latestCare != null) {
                        managerName = latestCare.getManagerAccount().getName();
                        managerPhone = latestCare.getManagerAccount().getPhoneNum();
                    }

                    if (senior.getSeniorStatus() != null) {
                        seniorState = SeniorStateDto.from(senior.getSeniorStatus());
                    }

                    return SeniorInfoDto.of(senior, managerName, managerPhone, seniorState);
                })
                .collect(Collectors.toList());
    }

}
