package org.farm.fireflyserver.domain.monitoring.web.dto;

import java.util.List;

public record MainHomeDto(
    SeniorCountDto seniorCount,
    SeniorLedStateCountDto seniorStateCount,
    MonthlyCareStateDto monthlyCareState,
    List<TownStateDto> townState
) {
    public static MainHomeDto of(SeniorCountDto seniorCount, SeniorLedStateCountDto seniorStateCount, MonthlyCareStateDto monthlyCareState, List<TownStateDto> townState) {

        return new MainHomeDto(seniorCount,seniorStateCount,monthlyCareState,townState);
    }
}
