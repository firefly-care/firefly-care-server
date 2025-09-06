package org.farm.fireflyserver.domain.monitoring.service;

import org.farm.fireflyserver.domain.monitoring.web.dto.*;

import java.util.List;

public interface MonitoringService {
    MainHomeDto getMainHome(String yearMonth, String calendarYearMonth, String calendarDate);

    MonthlyCareStateDto getMonthlyCareState(String yearMonth);
    SeniorLedStateCountDto getSeniorStateCount();
    List<ManagerStateDto> getManagerState();
    CalendarCareCountWithMonthDto getCalendarCareCount(String calendarYearMonth);
    CalendarCareStateWithDateDto getCalendarCareState(String calendarDate);
}
