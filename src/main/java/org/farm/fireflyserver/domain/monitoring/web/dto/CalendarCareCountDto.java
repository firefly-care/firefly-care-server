package org.farm.fireflyserver.domain.monitoring.web.dto;

public record CalendarCareCountDto(
        int date,
        int careCount
) {
    public static CalendarCareCountDto of(int date, int careCount) {
        return new CalendarCareCountDto(date, careCount);
    }
}
