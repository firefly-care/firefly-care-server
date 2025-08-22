package org.farm.fireflyserver.domain.monitoring.web.dto;

public record MainHomeDto(
    SeniorCountDto seniorCount
) {
    public static MainHomeDto of(SeniorCountDto seniorCount) {
        return new MainHomeDto(seniorCount);
    }
}
