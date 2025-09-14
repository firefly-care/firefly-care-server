package org.farm.fireflyserver.domain.monitoring.web.dto;

import java.time.LocalTime;

public record SeniorLedStateCountDto(
        LocalTime recentUpdateTime,
        int ledUseCount,
        //정상
        int normalCount,
        //관심
        int interestCount,
        //주의
        int cautionCount,
        //위험
        int dangerCount

) {
    public static SeniorLedStateCountDto of(LocalTime recentUpdateTime, int ledUseCount, int normalCount, int interestCount, int cautionCount, int dangerCount
    ) {
        return new SeniorLedStateCountDto(
                recentUpdateTime,
                ledUseCount,
                normalCount,
                interestCount,
                cautionCount,
                dangerCount
        );
    }
}
