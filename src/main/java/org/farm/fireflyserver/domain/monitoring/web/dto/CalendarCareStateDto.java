package org.farm.fireflyserver.domain.monitoring.web.dto;

import org.farm.fireflyserver.domain.care.persistence.entity.Care;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record CalendarCareStateDto(
        String seniorName,
        String careType,
        String careResult,
        String managerName,
        LocalDateTime careDateRaw,
        String careDate,
        String careContent
) {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy.MM.dd.(E)", Locale.KOREAN);
    public static CalendarCareStateDto from(Care care) {
        return new CalendarCareStateDto(
                care.getSenior().getName(),
                care.getType().getDesc(),
                care.getResult().getDesc(),
                care.getManagerAccount().getName(),
                care.getDate(),
                care.getDate().format(DATE_FMT),
                care.getContent()
        );
    }
}
