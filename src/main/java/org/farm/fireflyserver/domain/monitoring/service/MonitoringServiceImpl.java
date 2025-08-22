package org.farm.fireflyserver.domain.monitoring.service;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.monitoring.web.dto.ByTownCountDto;
import org.farm.fireflyserver.domain.monitoring.web.dto.MainHomeDto;
import org.farm.fireflyserver.domain.monitoring.web.dto.SeniorCountDto;
import org.farm.fireflyserver.domain.senior.persistence.repository.SeniorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonitoringServiceImpl  implements MonitoringService {

    private final SeniorRepository seniorRepository;

    @Override
    public MainHomeDto getMainHome() {
        SeniorCountDto seniorCount = getSeniorCount();
        return MainHomeDto.of(seniorCount);

    }

    // 대상자 수 조회
    private SeniorCountDto getSeniorCount() {
        int totalHouseCount = seniorRepository.countByIsActiveTrue();
        int amiUseCount =  seniorRepository.countByIsActiveTrueAndIsAmiUseTrue();
        int ledUseCount = seniorRepository.countByIsActiveTrueAndIsLedUseTrue();

        List<ByTownCountDto> byTownCount = getSeniorCountByTown();

        return SeniorCountDto.of(totalHouseCount, ledUseCount, amiUseCount, byTownCount);
    }

    // 읍면동별로 대상자 수
    private List<ByTownCountDto> getSeniorCountByTown() {
        List<Object[]> results = seniorRepository.countByTown();

        return results.stream()
                .map(row -> ByTownCountDto.of(
                        (String) row[0],
                        ((Long) row[1]).intValue()
                ))
                .toList();
    }
}
