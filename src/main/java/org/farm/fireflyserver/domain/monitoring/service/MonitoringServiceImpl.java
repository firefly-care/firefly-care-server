package org.farm.fireflyserver.domain.monitoring.service;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.care.persistence.CareRepository;
import org.farm.fireflyserver.domain.care.persistence.entity.Care;
import org.farm.fireflyserver.domain.monitoring.web.dto.*;
import org.farm.fireflyserver.domain.senior.persistence.entity.DangerLevel;
import org.farm.fireflyserver.domain.senior.persistence.repository.SeniorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonitoringServiceImpl implements MonitoringService {

    private final SeniorRepository seniorRepository;
    private final CareRepository careRepository;

    //메인홈 조회
    @Override
    public MainHomeDto getMainHome() {
        SeniorCountDto seniorCount = getSeniorCount();
        SeniorLedStateCountDto seniorStateCount = getSeniorStateCount();
        MonthlyCareStateDto monthlyCareState = getMonthlyCareState();
        List<TownStateDto> townState = getTownStateCount();
        return MainHomeDto.of(seniorCount, seniorStateCount, monthlyCareState, townState);

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

        int normalCount = 0, attentionCount = 0, cautionCount = 0, dangerCount = 0;

        for (Object[] row : results) {
            DangerLevel level = (DangerLevel) row[0];
            int count = ((Long) row[1]).intValue();

            if (level == null) level = DangerLevel.NORMAL;

            switch (level) {
                case NORMAL -> normalCount = count;
                case ATTENTION -> attentionCount = count;
                case CAUTION -> cautionCount = count;
                case DANGER -> dangerCount = count;
            }
        }

        int ledUseCount = seniorRepository.countByIsActiveTrueAndIsLedUseTrue();
        List<SeniorLedStateDto> seniorLedStates = convertToLedStateDto();

        return SeniorLedStateCountDto.of(ledUseCount, normalCount, attentionCount, cautionCount, dangerCount, seniorLedStates
        );
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
        String monthStr = String.format("%04d.%02d", year, month);

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

    //TODO : 필터링으로 처리
    // 지역별 대상자 상태 조회
    public List<TownStateDto> getTownStateCount() {
        Map<String, int[]> townMap = new HashMap<>();

        // 마지막 활동 시간 기준
        getLastActTimeCounts(townMap);

        // 이상징후 기준
        getStateCounts(townMap);

        // 위기 등급 기준
        getDangerLevelCounts(townMap);

        // 4. DTO 변환
        return townMap.entrySet().stream()
                .map(entry -> {
                    int[] c = entry.getValue();
                    return TownStateDto.of(
                            entry.getKey(),
                            c[0], c[1], c[2], c[3],   // 마지막 활동 시간
                            c[4], c[5], c[6], c[7],   // 이상징후
                            c[8], c[9], c[10], c[11]  // 위기등급
                    );
                })
                .toList();
    }

    private void getLastActTimeCounts(Map<String, int[]> townMap) {
        List<Object[]> results = seniorRepository.countByTownAndLastActTime();
        for (Object[] row : results) {
            String town = (String) row[0];
            Integer hours = (Integer) row[1];
            Long count = (Long) row[2];

            int[] counts = townMap.getOrDefault(town, new int[12]);
            int slot = getTimeSlotIndex(hours);
            counts[slot] += count.intValue();
            townMap.put(town, counts);
        }
    }

    //시간 차이 변환
    private int getTimeSlotIndex(int hours) {
        if (hours < 24) return 0;
        if (hours < 48) return 1;
        if (hours < 72) return 2;
        return 3;
    }

    private void getStateCounts(Map<String, int[]> townMap) {
        List<Object[]> results = seniorRepository.countByTownAndState();
        for (Object[] row : results) {
            String town = (String) row[0];
            String state = (String) row[1];
            Long count = (Long) row[2];

            if (state == null) continue;

            int[] counts = townMap.getOrDefault(town, new int[12]);
            switch (state) {
                case "수면장애" -> counts[4] += count.intValue();
                case "인지저하" -> counts[5] += count.intValue();
                case "무기력증" -> counts[6] += count.intValue();
                case "장시간 미활동" -> counts[7] += count.intValue();
            }
            townMap.put(town, counts);
        }
    }

    private void getDangerLevelCounts(Map<String, int[]> townMap) {
        List<Object[]> results = seniorRepository.countByTownAndDangerLevel();
        for (Object[] row : results) {
            String town = (String) row[0];

            DangerLevel level = (DangerLevel) row[1];

            Long count = (Long) row[2];

            int[] counts = townMap.getOrDefault(town, new int[12]);

            switch (level) {
                case NORMAL -> counts[8] += count.intValue();
                case ATTENTION -> counts[9] += count.intValue();
                case CAUTION -> counts[10] += count.intValue();
                case DANGER -> counts[11] += count.intValue();
            }

            townMap.put(town, counts);
        }
    }


}
