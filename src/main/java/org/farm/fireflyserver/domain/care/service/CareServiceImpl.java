package org.farm.fireflyserver.domain.care.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.exception.EntityNotFoundException;
import org.farm.fireflyserver.common.response.ErrorCode;
import org.farm.fireflyserver.domain.care.persistence.entity.Result;
import org.farm.fireflyserver.domain.care.persistence.entity.Type;
import org.farm.fireflyserver.domain.care.mapper.CareMapper;
import org.farm.fireflyserver.domain.care.persistence.AbsentResultRepository;
import org.farm.fireflyserver.domain.care.persistence.CareRepository;
import org.farm.fireflyserver.domain.care.persistence.CareResultRepository;
import org.farm.fireflyserver.domain.care.persistence.entity.AbsentResult;
import org.farm.fireflyserver.domain.care.persistence.entity.Care;
import org.farm.fireflyserver.domain.care.persistence.entity.CareResult;
import org.farm.fireflyserver.domain.care.web.dto.AbsentCareDetailsDto;
import org.farm.fireflyserver.domain.care.web.dto.CareDto;
import org.farm.fireflyserver.domain.care.web.dto.NormalCareDetailsDto;
import org.farm.fireflyserver.domain.manager.persistence.ManagerRepository;
import org.farm.fireflyserver.domain.manager.persistence.entity.Manager;
import org.farm.fireflyserver.domain.manager.web.dto.ManagerDto;
import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;
import org.farm.fireflyserver.domain.senior.persistence.repository.SeniorRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CareServiceImpl implements CareService {
    private final CareRepository careRepository;
    private final ManagerRepository managerRepository;
    private final SeniorRepository seniorRepository;
    private final CareResultRepository careResultRepository;
    private final AbsentResultRepository absentResultRepository;
    private final CareMapper careMapper;

    @Override
    public void addCare(CareDto.Register dto) {
        Manager manager = managerRepository.findById(dto.getManager_id())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        Senior senior = seniorRepository.findById(dto.getSenior_id())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SENIOR_NOT_FOUND));

        Result result;
        if ("COMPLETED".equalsIgnoreCase(dto.getResult())) {
            result = Result.NORMAL;
        } else {
            result = Result.from(dto.getResult());
        }
        
        Type type = Type.from(dto.getType());

        Care care = Care.builder()
                .content(dto.getContent())
                .manager(manager)
                .result(result)
                .senior(senior)
                .type(type)
                .build();

        careRepository.save(care);

        //돌봄이 등록 된 후 manager 테이블을 업데이트합니다.
        manager.addCare();

        if (dto.getDetails() instanceof NormalCareDetailsDto details) {
            CareResult careResult = careMapper.toCareResult(details, care);
            careResultRepository.save(careResult);
        } else if (dto.getDetails() instanceof AbsentCareDetailsDto details) {
            AbsentResult absentResult = careMapper.toAbsentResult(details, care);
            absentResultRepository.save(absentResult);
        }
    }

    @Override
    public List<CareDto.Response> getAllCare() {
        List<Care> cares = careRepository.findAll();

        return cares.stream()
                .map(CareDto.Response::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<CareDto.Response> searchCare(CareDto.SearchRequest dto) {
        List<Care> list = careRepository.search(dto);

        return list.stream()
                .map(CareDto.Response::from)
                .collect(Collectors.toList());
    }

    @Override
    public CareDto.MonthlyCare getSeniorMonthlyCare(Long seniorId, YearMonth yearMonth) {
        seniorRepository.findById(seniorId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SENIOR_NOT_FOUND));

        Map<Type, Long> cntPerType = careRepository.countCareByTypePerMonth(seniorId, yearMonth.getYear(), yearMonth.getMonthValue())
                .stream()
                .collect(Collectors.toMap(
                        row -> (Type) row[0],
                        row -> (Long) row[1]
                ));

        Long callCnt = cntPerType.getOrDefault(Type.CALL, 0L);
        Long visitCnt = cntPerType.getOrDefault(Type.VISIT, 0L);
        Long emergCnt = cntPerType.getOrDefault(Type.EMERGENCY, 0L);

        List<Care> caresInMonth = careRepository.findAllBySeniorSeniorIdAndDateBetween(
                seniorId,
                yearMonth.atDay(1).atStartOfDay(),
                yearMonth.atEndOfMonth().atTime(23, 59, 59)
        );

        List<CareDto.MonthlyCare.CareTuple> careTuples = caresInMonth.stream()
                .map(care -> new CareDto.MonthlyCare.CareTuple(
                        care.getDate(),
                        care.getType(),
                        care.getResult()
                ))
                .collect(Collectors.toList());

        return new CareDto.MonthlyCare(callCnt, visitCnt, emergCnt, careTuples);
    }

    @Override
    public List<Long> getSeniorIdsByManagerId(Long managerId) {
        return careRepository.findDistinctSeniorIdsByManagerId(managerId);
    }

    @Override
    public List<ManagerDto.CareSeniorInfo> getCareSeniorInfoByManagerAndCareType(Long managerId, Type careType) {
        return careRepository.getCareSeniorInfoByManagerAndCareType(managerId, careType);
    }
}
