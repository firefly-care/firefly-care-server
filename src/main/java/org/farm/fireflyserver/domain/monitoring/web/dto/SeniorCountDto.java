package org.farm.fireflyserver.domain.monitoring.web.dto;

import java.util.List;

public record SeniorCountDto(
        //전체 가구수
        int totalCount,
        //LED 가구수
        int ledUseCount,
        //Ami 가구수
        int amiUseCount,

        List<ByTownCountDto> byTownCount
) {
    public static SeniorCountDto of(int totalCount, int ledUseCount, int amiUseCount, List<ByTownCountDto> byTownCount) {
        return new SeniorCountDto(totalCount, ledUseCount, amiUseCount, byTownCount);
    }
}
