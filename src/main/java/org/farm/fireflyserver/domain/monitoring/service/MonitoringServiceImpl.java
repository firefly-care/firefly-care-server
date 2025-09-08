package org.farm.fireflyserver.domain.monitoring.service;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.account.persistence.AccountRepository;
import org.farm.fireflyserver.domain.account.persistence.entity.Account;
import org.farm.fireflyserver.domain.account.persistence.entity.Authority;
import org.farm.fireflyserver.domain.care.persistence.CareRepository;
import org.farm.fireflyserver.domain.care.persistence.entity.Care;
import org.farm.fireflyserver.domain.monitoring.web.dto.*;
import org.farm.fireflyserver.domain.senior.persistence.entity.DangerLevel;
import org.farm.fireflyserver.domain.senior.persistence.repository.SeniorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonitoringServiceImpl implements MonitoringService {

    private final SeniorRepository seniorRepository;
    private final CareRepository careRepository;
    private final AccountRepository accountRepository;

    // 공통 날짜 포맷터
    private static final DateTimeFormatter YM_FMT  = DateTimeFormatter.ofPattern("yyyy.MM");
    private static final DateTimeFormatter YMD_FMT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private static LocalDateTime startOfMonth(YearMonth ym) {return ym.atDay(1).atStartOfDay();}
    private static LocalDateTime endOfMonth(YearMonth ym) {return ym.atEndOfMonth().atTime(23, 59, 59);}
    private static LocalDateTime startOfDay(LocalDate d) {return d.atStartOfDay();}
    private static LocalDateTime endOfDay(LocalDate d) {return d.atTime(23, 59, 59);}


    //메인홈 조회
    @Override
    public MainHomeDto getMainHome(String yearMonth, String calendarYearMonth, String calendarDate) {
        // 월별 돌봄 현황
        MonthlyCareStateDto monthlyCareState = getMonthlyCareState(yearMonth);
        // LED 이상 탐지 현황
        SeniorLedStateCountDto seniorStateCount = getSeniorStateCount();
        // 담당자 현황
        List<ManagerStateDto> managerStateDto = getManagerState();
        // 캘린더 돌봄 현황
        CalendarCareCountWithMonthDto calendarCareCount = getCalendarCareCount(calendarYearMonth);
        // 캘린더 돌봄 내역
        CalendarCareStateWithDateDto calendarCareState = getCalendarCareState(calendarDate);

        return MainHomeDto.of(monthlyCareState, seniorStateCount,managerStateDto,calendarCareCount,calendarCareState);

    }

    // 월별 돌봄 현황
    public MonthlyCareStateDto getMonthlyCareState(String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth, YM_FMT);
        List<Care> cares = careRepository.findAllByDateBetween(startOfMonth(ym), endOfMonth(ym));

        int totalSeniorCount = seniorRepository.countByIsActiveTrue();
        int totalManagerCount = accountRepository.countByAuthority(Authority.MNG);

        int call = 0, visit = 0, emergency = 0;
        for (Care c : cares) {
            switch (c.getType()) {
                case CALL -> call++;
                case VISIT -> visit++;
                case EMERGENCY -> emergency++;
            }
        }
        int totalCare = call + visit + emergency;

        return MonthlyCareStateDto.of(yearMonth, totalSeniorCount, totalManagerCount, totalCare, call, visit, emergency);
    }

    // Led 이상 탐지 현황
    public SeniorLedStateCountDto getSeniorStateCount() {
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

        return SeniorLedStateCountDto.of(ledUseCount, normalCount, attentionCount, cautionCount, dangerCount);
    }

    // 담당자 현황
    public List<ManagerStateDto> getManagerState() {
        List<Account> managers = accountRepository.findAllByAuthority(Authority.MNG);

        return managers.stream().map(manager -> {
            long careCount = careRepository.countByManagerAccount(manager);
            long seniorCount = careRepository.countDistinctSeniorByManagerAccount(manager);
            Care recentCare = careRepository.findTopByManagerAccountOrderByDateDesc(manager);
            String recentCareDate = formatRecentCareDate(recentCare != null ? recentCare.getDate() : null);
            return new ManagerStateDto(manager.getName(), (int) seniorCount, (int) careCount, recentCareDate);
        }).toList();
    }

    private String formatRecentCareDate(LocalDateTime date) {
        if (date == null) return "기록 없음";
        Duration duration = Duration.between(date, LocalDateTime.now());
        long hours = duration.toHours();
        long days = duration.toDays();
        return (hours < 24) ? hours + "시간 전" : days + "일 전";
    }


    // 캘린더 돌봄 현황
    public CalendarCareCountWithMonthDto getCalendarCareCount(String calendarYearMonth) {
        YearMonth ym = YearMonth.parse(calendarYearMonth, YM_FMT);
        List<Object[]> rows = careRepository.countByDateBetweenGroupByDate(startOfMonth(ym), endOfMonth(ym));

        List<CalendarCareCountDto> items = rows.stream()
                .map(row -> {
                    Object d0 = row[0];
                    LocalDate day = (d0 instanceof LocalDate ld) ? ld : ((java.sql.Date) d0).toLocalDate();
                    int count = ((Number) row[1]).intValue();
                    return CalendarCareCountDto.of(day.getDayOfMonth(), count);
                })
                .toList();

        return CalendarCareCountWithMonthDto.of(calendarYearMonth, items);
    }



    // 캘린더 돌봄 내역
    public CalendarCareStateWithDateDto getCalendarCareState(String calendarDate) {
        LocalDate target = LocalDate.parse(calendarDate, YMD_FMT);
        List<Care> cares = careRepository.findAllByDateBetweenOrderByDateDesc(startOfDay(target), endOfDay(target));

        List<CalendarCareStateDto> items = cares.stream()
                .map(CalendarCareStateDto::from)
                .toList();

        return CalendarCareStateWithDateDto.of(calendarDate, items);
    }
}

   /* ==================================================================
     * 안쓰는 함수 V1
     * ==================================================================

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

*/

