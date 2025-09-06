package org.farm.fireflyserver.domain.monitoring.web.dto;

import java.util.List;

public record CalendarCareStateWithDate(
        String careStateDate,
        List<CalendarCareStateDto> careState
) {
    public static CalendarCareStateWithDate of(String careStateDate,
                                               List<CalendarCareStateDto> careState) {
        return new CalendarCareStateWithDate(careStateDate, careState);
    }
}
