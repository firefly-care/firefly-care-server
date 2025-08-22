package org.farm.fireflyserver.domain.monitoring.web.dto;

public record MainHomeDto(
    SeniorCountDto seniorCount,
    SeniorLedStateCountDto seniorStateCount
) {
    public static MainHomeDto of(SeniorCountDto seniorCount, SeniorLedStateCountDto seniorStateCount) {

        return new MainHomeDto(seniorCount,seniorStateCount);
    }
}
