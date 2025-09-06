package org.farm.fireflyserver.domain.monitoring.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.farm.fireflyserver.domain.monitoring.service.MonitoringService;
import org.farm.fireflyserver.domain.monitoring.web.dto.MainHomeDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/monitoring")
@Tag(name = "Monitoring", description = "모니터링/대시보드 관련 API")
public class MonitoringController {

    private final MonitoringService monitoringService;

    @GetMapping("/main")
    @Operation(summary = "메인 홈 모니터링 정보 조회",
            description = "메인 홈 모니터링 정보 조회(레이아웃별로 구성된 정보 반환) + \n")
    public BaseResponse<?> getMainHome( @RequestParam(required = false) String yearMonth,
                                        @RequestParam(required = false) String calendarYearMonth,
                                        @RequestParam(required = false) String calendarDate) {

        // 오늘 날짜 기준 기본 값
        LocalDate today = LocalDate.now();
        DateTimeFormatter ymdFmt  = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        yearMonth = (yearMonth == null || yearMonth.isBlank())? today.format(ymdFmt) : yearMonth;
        calendarYearMonth = (calendarYearMonth == null || calendarYearMonth.isBlank()) ? today.format(ymdFmt) : calendarYearMonth;
        calendarDate = (calendarDate == null || calendarDate.isBlank()) ? today.format(ymdFmt) : calendarDate;

        MainHomeDto mainHome = monitoringService.getMainHome(yearMonth, calendarYearMonth, calendarDate);
        return BaseResponse.of(SuccessCode.OK, mainHome);
    }
}
