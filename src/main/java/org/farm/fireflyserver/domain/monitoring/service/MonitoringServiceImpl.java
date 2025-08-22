package org.farm.fireflyserver.domain.monitoring.service;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.care.persistence.CareRepository;
import org.farm.fireflyserver.domain.care.persistence.entity.Care;
import org.farm.fireflyserver.domain.monitoring.web.dto.*;
import org.farm.fireflyserver.domain.senior.persistence.repository.SeniorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonitoringServiceImpl implements MonitoringService {

    private final SeniorRepository seniorRepository;
    private final CareRepository careRepository;

    @Override
    public MainHomeDto getMainHome() {
        SeniorCountDto seniorCount = getSeniorCount();
        SeniorLedStateCountDto seniorStateCount = getSeniorStateCount();
        MonthlyCareStateDto monthlyCareState = getMonthlyCareState();
        return MainHomeDto.of(seniorCount, seniorStateCount,monthlyCareState);

    }

    // 대상자 수 조회
    private SeniorCountDto getSeniorCount() {
        int totalCount = seniorRepository.countByIsActiveTrue();
        int amiUseCount = seniorRepository.countByIsActiveTrueAndIsAmiUseTrue();
        int ledUseCount = seniorRepository.countByIsActiveTrueAndIsLedUseTrue();

        List<ByTownCountDto> byTownCount = getSeniorCountByTown();

        return SeniorCountDto.of(totalCount, ledUseCount, amiUseCount, byTownCount);
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
        int normalCount = 0, interestCount = 0, cautionCount = 0, dangerCount = 0;

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

    // 월별 돌봄 현황
    public MonthlyCareStateDto getMonthlyCareState() {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        List<Care> cares = getCaresByMonth(year, month);

        int totalCount = seniorRepository.countByIsActiveTrue();
        int callCount = 0, visitCount = 0, emergencyCount = 0;

        for (Care care : cares) {
            switch (care.getType()) {
                case CALL -> callCount++;
                case VISIT -> visitCount++;
                case EMERGENCY -> emergencyCount++;
            }
        }

        int careCount = callCount + visitCount + emergencyCount;
        String monthStr = String.format("%04d.%02d", year, month);

        return MonthlyCareStateDto.of(monthStr, totalCount, careCount, callCount, visitCount, emergencyCount);
    }

    // 월별 돌봄 기록 조회
    private List<Care> getCaresByMonth(int year, int month) {
        YearMonth targetMonth = YearMonth.of(year, month);
        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();

        return careRepository.findAllByDateBetween(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );
    }

}
