package org.farm.fireflyserver.domain.senior.service;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.exception.EntityNotFoundException;
import org.farm.fireflyserver.common.response.ErrorCode;
import org.farm.fireflyserver.domain.account.persistence.entity.Account;
import org.farm.fireflyserver.domain.care.persistence.entity.Care;
import org.farm.fireflyserver.domain.led.web.dto.response.LedStateDto;
import org.farm.fireflyserver.domain.manager.persistence.ManagerRepository;
import org.farm.fireflyserver.domain.manager.persistence.entity.Manager;
import org.farm.fireflyserver.domain.manager.web.dto.ManagerDto;
import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;
import org.farm.fireflyserver.domain.senior.persistence.entity.SeniorStatus;
import org.farm.fireflyserver.domain.senior.persistence.repository.SeniorRepository;
import org.farm.fireflyserver.domain.senior.web.dto.request.RegisterSeniorDto;
import org.farm.fireflyserver.domain.senior.web.dto.request.RequestSeniorDto;
import org.farm.fireflyserver.domain.senior.web.dto.response.SeniorDetailDto;
import org.farm.fireflyserver.domain.senior.web.dto.response.SeniorInfoDto;
import org.farm.fireflyserver.domain.senior.web.dto.response.SeniorStateDto;
import org.farm.fireflyserver.domain.senior.web.mapper.SeniorMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.farm.fireflyserver.common.response.ErrorCode.MANAGER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeniorServiceImpl implements SeniorService {

    private final SeniorRepository seniorRepository;
    private final ManagerRepository managerRepository;
    private final SeniorMapper seniorMapper;

    // 대상자 등록
    @Transactional
    public void registerSenior(RegisterSeniorDto dto) {
        Senior senior = seniorMapper.toEntity(dto);

        String managerName = dto.managerName();
        Manager manager = managerRepository.findByName(managerName)
                .orElseThrow(() -> new EntityNotFoundException(MANAGER_NOT_FOUND));

        senior.assignManager(manager);
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
                    List<LedStateDto> ledState = getLedStates(senior);

                    return SeniorInfoDto.of(senior, manager[0], manager[1], seniorState, ledState);
                })
                .collect(Collectors.toList());
    }

    // 대상자 담당자 정보
    private String[] getManagerInfo(Senior senior) {
        Manager manager = senior.getManager();

        String name = manager.getName();
        String phone = manager.getPhoneNum();
        return new String[]{
                name != null ? name : "",
                phone != null ? phone : ""
        };
    }

    private SeniorStateDto getSeniorState(Senior senior) {
        return senior.getSeniorStatus() != null
                ? SeniorStateDto.from(senior.getSeniorStatus())
                : null;
    }

    private List<LedStateDto> getLedStates(Senior senior) {
        return senior.getLedStates().stream()
                .map(LedStateDto::of)
                .collect(Collectors.toList());
    }

    // 대상자 검색
    @Override
    public List<SeniorInfoDto> searchSeniors(Boolean isActive, String keywordType, String keyword) {
        List<Senior> seniors = seniorRepository.searchSeniors(isActive, keywordType, keyword);

        return seniors.stream()
                .map(s -> {
                    String[] manager = getManagerInfo(s);
                    SeniorStateDto seniorState = getSeniorState(s);
                    return SeniorInfoDto.of(s, manager[0], manager[1], seniorState, getLedStates(s));
                })
                .collect(Collectors.toList());
    }

    @Override
    public SeniorDetailDto getSeniorDetail(Long seniorId) {
        Senior senior = seniorRepository.findSeniorDetailById(seniorId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND));

        Manager manager = senior.getManager();

        SeniorStatus seniorStatus = senior.getSeniorStatus();
        if (seniorStatus == null) {
            seniorStatus = new SeniorStatus();
        }

        return SeniorDetailDto.fromEntities(senior, seniorStatus, manager.getAccount());
    }

    @Transactional
    @Override
    public void deactivateSenior(RequestSeniorDto.Deactivate dto) {
        Senior senior = seniorRepository.findById(dto.seniorId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SENIOR_NOT_FOUND));
        senior.deactivate();
    }


    @Override
    public List<ManagerDto.SeniorInfo> getSeniorInfoByIds(List<Long> seniorIds) {
        return seniorRepository.findAllById(seniorIds).stream()
                .map(senior -> {
                    long age = Period.between(senior.getBirthday(), LocalDate.now()).getYears();
                    return new ManagerDto.SeniorInfo(
                            senior.getSeniorId(),
                            senior.getName(),
                            senior.getGender(),
                            age,
                            senior.getAddress()
                    );
                })
                .collect(Collectors.toList());
    }
}