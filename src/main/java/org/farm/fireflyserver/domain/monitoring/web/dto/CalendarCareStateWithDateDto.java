package org.farm.fireflyserver.domain.monitoring.web.dto;

import java.util.List;

public record CalendarCareStateWithDateDto(
        String date,
        List<CalendarCareStateDto> careState
) {
    public static CalendarCareStateWithDateDto of(String date,
                                               List<CalendarCareStateDto> careState) {
        return new CalendarCareStateWithDateDto(date, careState);
    }
}
