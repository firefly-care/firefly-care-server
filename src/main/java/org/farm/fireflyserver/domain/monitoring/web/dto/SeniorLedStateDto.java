package org.farm.fireflyserver.domain.monitoring.web.dto;

import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;

public record SeniorLedStateDto(
        String name,
        String managerName,
        String dangerLevel,
        int lastActTime,
        String state
) {
    public static SeniorLedStateDto from(Senior senior, String managerName) {
        return new SeniorLedStateDto(
                senior.getName(),
                managerName,
                senior.getSeniorStatus().getDangerLevel(),
                senior.getSeniorStatus().getLastActTime(),
                senior.getSeniorStatus().getState()
        );
    }
}
