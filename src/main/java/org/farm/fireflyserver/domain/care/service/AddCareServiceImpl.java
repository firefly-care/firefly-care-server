package org.farm.fireflyserver.domain.care.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.exception.EntityNotFoundException;
import org.farm.fireflyserver.common.response.ErrorCode;
import org.farm.fireflyserver.domain.care.Result;
import org.farm.fireflyserver.domain.care.Type;
import org.farm.fireflyserver.domain.care.mapper.CareMapper;
import org.farm.fireflyserver.domain.care.persistence.AccountRepository;
import org.farm.fireflyserver.domain.care.persistence.AbsentResultRepository;
import org.farm.fireflyserver.domain.care.persistence.CareRepository;
import org.farm.fireflyserver.domain.care.persistence.CareResultRepository;
import org.farm.fireflyserver.domain.care.persistence.SeniorRepository;
import org.farm.fireflyserver.domain.care.persistence.entity.AbsentResult;
import org.farm.fireflyserver.domain.care.persistence.entity.Account;
import org.farm.fireflyserver.domain.care.persistence.entity.Care;
import org.farm.fireflyserver.domain.care.persistence.entity.CareResult;
import org.farm.fireflyserver.domain.care.persistence.entity.Senior;
import org.farm.fireflyserver.domain.care.web.dto.AbsentCareDetailsDto;
import org.farm.fireflyserver.domain.care.web.dto.CareDTO;
import org.farm.fireflyserver.domain.care.web.dto.NormalCareDetailsDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AddCareServiceImpl implements AddCareService {
    private final CareRepository careRepository;
    private final AccountRepository accountRepository;
    private final SeniorRepository seniorRepository;
    private final CareResultRepository careResultRepository;
    private final AbsentResultRepository absentResultRepository;
    private final CareMapper careMapper;

    @Override
    public void addCare(CareDTO.Register dto) {
        Account manager = accountRepository.findById(dto.getManager_id())
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
                .managerAccount(manager)
                .result(result)
                .senior(senior)
                .type(type)
                .build();

        careRepository.save(care);

        if (dto.getDetails() instanceof NormalCareDetailsDto details) {
            CareResult careResult = careMapper.toCareResult(details, care);
            careResultRepository.save(careResult);
        } else if (dto.getDetails() instanceof AbsentCareDetailsDto details) {
            AbsentResult absentResult = careMapper.toAbsentResult(details, care);
            absentResultRepository.save(absentResult);
        }
    }

    @Override
    public List<CareDTO.Response> getAllCare() {
        List<Care> cares = careRepository.findAll();

        return cares.stream()
                .map(CareDTO.Response::from)
                .collect(Collectors.toList());
    }
}
