package org.farm.fireflyserver.domain.monitoring.service;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.monitoring.web.dto.*;
import org.farm.fireflyserver.domain.senior.persistence.repository.SeniorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonitoringServiceImpl implements MonitoringService {

    private final SeniorRepository seniorRepository;

    @Override
    public MainHomeDto getMainHome() {
        SeniorCountDto seniorCount = getSeniorCount();
        SeniorLedStateCountDto seniorStateCount = getSeniorStateCount();
        return MainHomeDto.of(seniorCount, seniorStateCount);

    }

    // 대상자 수 조회
    private SeniorCountDto getSeniorCount() {
        int totalHouseCount = seniorRepository.countByIsActiveTrue();
        int amiUseCount = seniorRepository.countByIsActiveTrueAndIsAmiUseTrue();
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

    // Led 이상 탐지 현황
    private SeniorLedStateCountDto getSeniorStateCount() {
        List<Object[]> results = seniorRepository.countByDangerLevel();
        int ledUseCount = seniorRepository.countByIsActiveTrueAndIsLedUseTrue();

        //이상 탐지 통계
        int[] counts = calculateStateCounts(results);

        // 대상자별 상태 정보
        List<SeniorLedStateDto> seniorLedStates = convertToLedStateDto();

        return SeniorLedStateCountDto.of(ledUseCount, counts[0], counts[1], counts[2], counts[3], seniorLedStates);
    }

    private int[] calculateStateCounts(List<Object[]> results) {
        int normalCount = 0;
        int interestCount = 0;
        int cautionCount = 0;
        int dangerCount = 0;

        for (Object[] row : results) {
            String dangerLevel = (String) row[0];
            int count = ((Long) row[1]).intValue();

            //enum으로 수정 필요
            if (dangerLevel == null || dangerLevel.equals("정상")) {
                normalCount = count;
            } else if (dangerLevel.equals("관심")) {
                interestCount = count;
            } else if (dangerLevel.equals("위험")) {
                dangerCount = count;
            } else if (dangerLevel.equals("주의")) {
                cautionCount = count;
            }
        }

        return new int[]{normalCount, interestCount, cautionCount, dangerCount};
    }

    private List<SeniorLedStateDto> convertToLedStateDto() {
        return seniorRepository.findByIsActiveTrueAndIsLedUseTrue().stream()
                .map(senior -> SeniorLedStateDto.from(
                        senior,
                        senior.getCareList().stream()
                                .map(care -> care.getManagerAccount().getName())
                                .findFirst()
                                .orElse(null)
                ))
                .toList();
    }

}
