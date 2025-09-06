package org.farm.fireflyserver.domain.monitoring.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MainHomeDto(

        @Schema(description = "월간 돌봄 현황")
        MonthlyCareStateDto monthlyCareState,

        @Schema(description = "LED 이상 탐지 현황")
        SeniorLedStateCountDto seniorStateCount,

        @Schema(description = "담당자 현황")
        List<ManagerStateDto> managerState

) {
    public static MainHomeDto of(MonthlyCareStateDto monthlyCareState,
                                 SeniorLedStateCountDto seniorStateCount,
                                 List<ManagerStateDto> managerState) {
        return new MainHomeDto(monthlyCareState, seniorStateCount, managerState);
    }
}
