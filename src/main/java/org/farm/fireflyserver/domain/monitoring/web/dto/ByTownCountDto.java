package org.farm.fireflyserver.domain.monitoring.web.dto;

public record ByTownCountDto(
        String townName,
        int totalCountbyTown
) {
    public static ByTownCountDto of(String townName, int totalCountbyTown) {
        return new ByTownCountDto(townName, totalCountbyTown);
    }
}
