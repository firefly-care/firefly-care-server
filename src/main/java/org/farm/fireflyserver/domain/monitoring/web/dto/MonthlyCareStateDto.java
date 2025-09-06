package org.farm.fireflyserver.domain.monitoring.web.dto;

public record MonthlyCareStateDto(
        String month,
        int totalSeniorCount,
        int totalManagerCount,
        int totalCareCount,
        int callCount,
        int visitCount,
        int emergencyCount
) {
    public static MonthlyCareStateDto of(String month,
                                       int totalSeniorCount,
                                       int totalManagerCount,
                                       int totalCareCount,
                                       int callCount,
                                       int visitCount,
                                       int emergencyCount) {
        return new MonthlyCareStateDto(month, totalSeniorCount, totalManagerCount, totalCareCount, callCount, visitCount, emergencyCount);
    }
}
