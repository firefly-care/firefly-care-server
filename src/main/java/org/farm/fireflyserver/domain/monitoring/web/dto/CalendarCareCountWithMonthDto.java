package org.farm.fireflyserver.domain.monitoring.web.dto;

import java.util.List;

public record CalendarCareCountWithMonthDto(
        String month,
        List<CalendarCareCountDto> calendarCareCount
) {
    public static CalendarCareCountWithMonthDto of(String month,
                                                  List<CalendarCareCountDto> calendarCareCount) {
        return new CalendarCareCountWithMonthDto(month, calendarCareCount);
    }
}
