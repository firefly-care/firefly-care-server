package org.farm.fireflyserver.domain.monitoring.web.dto;

import org.farm.fireflyserver.domain.senior.web.dto.response.SeniorStateDto;

import java.util.List;

public record SeniorLedStateCountDto(
        int ledUseCount,

        //정상
        int normalCount,
        //관심
        int interestCount,
        //주의
        int cautionCount,
        //위험
        int dangerCount,

        List<SeniorLedStateDto> seniorLedState
) {
    public static SeniorLedStateCountDto of(int ledUseCount, int normalCount, int interestCount, int cautionCount, int dangerCount, List<SeniorLedStateDto> seniorLedState
    ) {
        return new SeniorLedStateCountDto(
                ledUseCount,
                normalCount,
                interestCount,
                cautionCount,
                dangerCount,
                seniorLedState
        );
    }
}
