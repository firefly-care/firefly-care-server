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

        //돌봄 내용
        String eatingState,
        String cognitionState,
        String communicationState,
        String healthState

) {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy.MM.dd.(E)", Locale.KOREAN);
    public static CalendarCareStateDto from(Care care) {
        return new CalendarCareStateDto(
                care.getSenior().getName(),
                care.getType().getDesc(),
                care.getResult().getDesc(),
                care.getManager().getName(),
                care.getDate(),
                care.getDate().format(DATE_FMT),
                care.getCareResult() != null ? care.getCareResult().getEating().getDesc() : null,
                care.getCareResult() != null ? care.getCareResult().getCognition().getDesc() : null,
                care.getCareResult() != null ? care.getCareResult().getCommunication().getDesc() : null,
                care.getCareResult() != null ? care.getCareResult().getHealth().getDesc() : null
        );
    }
}
