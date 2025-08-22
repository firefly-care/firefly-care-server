package org.farm.fireflyserver.domain.senior.web.dto.response;

import org.farm.fireflyserver.domain.senior.persistence.entity.SeniorStatus;

public record SeniorStateDto(
        Integer lastActTime,
        String state
) {
    public static SeniorStateDto from(SeniorStatus seniorStatus) {
        return new SeniorStateDto(
                seniorStatus.getLastActTime(),
                seniorStatus.getState()
        );
    }

}
