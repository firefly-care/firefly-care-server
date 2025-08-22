package org.farm.fireflyserver.domain.monitoring.web.dto;

public record MonthlyCareStateDto(
        String month,
        int totalCount,
        int careCount,
        int callCount,
        int visitCount,
        int emergencyCount
) {
    public static MonthlyCareStateDto of(String month, int totalCount, int careCount, int callCount, int visitCount, int emergencyCount) {
        return new MonthlyCareStateDto(month,totalCount, careCount, callCount, visitCount, emergencyCount);
    }
}
