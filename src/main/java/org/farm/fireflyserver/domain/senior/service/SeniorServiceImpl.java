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

    // 대상자 목록 정보 조회
    @Override
    public List<SeniorInfoDto> getSeniorInfo() {
        List<Senior> seniors = seniorRepository.findAll();

        return seniors.stream()
                .map(senior -> {
                    String[] manager = getManagerInfo(senior);
                    SeniorStateDto seniorState = getSeniorState(senior);

                    return SeniorInfoDto.of(senior, manager[0], manager[1], seniorState);
                })
                .collect(Collectors.toList());
    }

    private String[] getManagerInfo(Senior senior) {
        // 가장 최근 돌봄 이력
        Care latestCare = senior.getCareList().stream()
                .max(Comparator.comparing(Care::getDate))
                .orElse(null);

        if (latestCare == null) {
            return new String[]{null, null};
        }

        return new String[]{
                latestCare.getManagerAccount().getName(),
                latestCare.getManagerAccount().getPhoneNum()
        };
    }

    private SeniorStateDto getSeniorState(Senior senior) {
        return senior.getSeniorStatus() != null
                ? SeniorStateDto.from(senior.getSeniorStatus())
                : null;
    }


    // 대상자 검색
    @Override
    public List<SeniorInfoDto> searchSeniors(Boolean isActive, String keywordType, String keyword) {
        List<Senior> seniors = seniorRepository.searchSeniors(isActive, keywordType, keyword);

        return seniors.stream()
                .map(s -> {
                    String[] manager = getManagerInfo(s);
                    SeniorStateDto seniorState = getSeniorState(s);
                    return SeniorInfoDto.of(s, manager[0], manager[1], seniorState);
                })
                .collect(Collectors.toList());
    }


}
