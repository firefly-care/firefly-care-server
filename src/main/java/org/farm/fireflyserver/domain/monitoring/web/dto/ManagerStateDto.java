package org.farm.fireflyserver.domain.monitoring.web.dto;

public record ManagerStateDto(
        String managerName,
        int seniorCount,
        int careCount,
        String recentCareDate
) {
}
