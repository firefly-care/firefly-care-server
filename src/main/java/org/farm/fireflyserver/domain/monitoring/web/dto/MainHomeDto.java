package org.farm.fireflyserver.domain.monitoring.web.dto;

public record MainHomeDto(
    SeniorCountDto seniorCount,
    SeniorLedStateCountDto seniorStateCount,
    MonthlyCareStateDto monthlyCareState
) {
    public static MainHomeDto of(SeniorCountDto seniorCount, SeniorLedStateCountDto seniorStateCount,    MonthlyCareStateDto monthlyCareState) {

        return new MainHomeDto(seniorCount,seniorStateCount,monthlyCareState);
    }
}
