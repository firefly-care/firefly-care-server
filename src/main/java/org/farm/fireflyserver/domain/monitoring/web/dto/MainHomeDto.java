package org.farm.fireflyserver.domain.monitoring.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MainHomeDto(
        @Schema(description = "서비스 대상자 현황")
        SeniorCountDto seniorCount,

        @Schema(description = "LED 이상 탐지 현황")
        SeniorLedStateCountDto seniorStateCount,

        @Schema(description = "월간 돌봄 현황")
        MonthlyCareStateDto monthlyCareState,

        @Schema(description = "지역별 대상자 상태 현황")
        List<TownStateDto> townState
) {
    public static MainHomeDto of(SeniorCountDto seniorCount, SeniorLedStateCountDto seniorStateCount, MonthlyCareStateDto monthlyCareState, List<TownStateDto> townState) {

        return new MainHomeDto(seniorCount,seniorStateCount,monthlyCareState,townState);
    }
}
