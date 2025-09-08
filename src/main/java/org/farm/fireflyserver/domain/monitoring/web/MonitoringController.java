package org.farm.fireflyserver.domain.monitoring.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
public class MonitoringController {

    private final MonitoringService monitoringService;

    // 통합 현황 조회 API
    @Tag(name = "MainHome", description = "메인 홈 통합 API")
    @GetMapping("/main")
    @Operation(summary = "(통합) 메인 홈 모니터링 정보 조회",
            description = "`개별 현황 업데이트는 각 전용 API를 사용하고(옵션)` 초기 접속 시에는 본 API를 호출하여 전체 데이터를 한 번에 조회. <br>" +
                    "레이아웃별로(월간 돌봄 현황, LED 이상 탐지 현황, 담당자 현황,캘린더 돌봄 현황, 내역) 구성된 정보 반환. <br> " +
                    "파라미터 없을 시 오늘 날짜 기준으로 기본 값 설정"
    )
    public BaseResponse<?> getMainHome(
            @Parameter(
                    description = "월간 돌봄 현황 조회 기준 (yyyy.MM).",
                    example = "2025.09"
            )
            @RequestParam(required = false) String yearMonth,

            @Parameter(
                    description = "캘린더 월간 돌봄 현황 조회 기준 (yyyy.MM). ",
                    example = "2025.09"
            )
            @RequestParam(required = false) String calendarYearMonth,

            @Parameter(
                    description = "캘린더 일간 돌봄 내역 조회 기준 (yyyy.MM.dd).",
                    example = "2025.09.07"
            )
            @RequestParam(required = false) String calendarDate
    ) {

        // 오늘 날짜 기준 기본 값 설정
        LocalDate today = LocalDate.now();
        DateTimeFormatter ymFmt = DateTimeFormatter.ofPattern("yyyy.MM");
        DateTimeFormatter ymdFmt = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        yearMonth = (yearMonth == null || yearMonth.isBlank()) ? today.format(ymFmt) : yearMonth;
        calendarYearMonth = (calendarYearMonth == null || calendarYearMonth.isBlank()) ? today.format(ymFmt) : calendarYearMonth;
        calendarDate = (calendarDate == null || calendarDate.isBlank()) ? today.format(ymdFmt) : calendarDate;

        MainHomeDto mainHome = monitoringService.getMainHome(yearMonth, calendarYearMonth, calendarDate);
        return BaseResponse.of(SuccessCode.OK, mainHome);
    }

    /* 개별 현황 업데이트 API */

    // 월간 돌봄 현황
    @Tag(name = "MainHome-업데이트용", description = "메인 홈 개별 API")
    @GetMapping("/monthly-care")
    @Operation(summary = "1. 월간 돌봄 현황 조회")
    public BaseResponse<?> getMonthlyCareState(
            @Parameter(description = "조회 기준 (yyyy.MM).", example = "2025.09")
            @RequestParam String yearMonth
    ) {
        return BaseResponse.of(SuccessCode.OK, monitoringService.getMonthlyCareState(yearMonth));
    }

    // LED 이상 탐지 현황
    @Tag(name = "MainHome-업데이트용", description = "메인 홈 개별 API")
    @GetMapping("/led-state")
    @Operation(summary = "2. LED 이상 탐지 현황 조회")
    public BaseResponse<?> getSeniorLedStateCount() {
        return BaseResponse.of(SuccessCode.OK, monitoringService.getSeniorStateCount());
    }

    // 담당자 현황
    @Tag(name = "MainHome-업데이트용", description = "메인 홈 개별 API")
    @GetMapping("/manager-state")
    @Operation(summary = "3. 담당자 현황 조회")
    public BaseResponse<?> getManagerState() {
        return BaseResponse.of(SuccessCode.OK, monitoringService.getManagerState());
    }

    // 캘린더 돌봄 현황
    @Tag(name = "MainHome-업데이트용", description = "메인 홈 개별 API")
    @GetMapping("/calendar-count")
    @Operation(summary = "4. 캘린더 월간 돌봄 현황 조회")
    public BaseResponse<?> getCalendarCareCount(
            @Parameter(description = "조회 기준 (yyyy.MM).", example = "2025.09")
            @RequestParam String calendarYearMonth
    ) {
        return BaseResponse.of(SuccessCode.OK, monitoringService.getCalendarCareCount(calendarYearMonth));
    }

    // 캘린더 돌봄 내역
    @Tag(name = "MainHome-업데이트용", description = "메인 홈 개별 API")
    @GetMapping("/calendar-state")
    @Operation(summary = "5. 캘린더 일간 돌봄 내역 조회")
    public BaseResponse<?> getCalendarCareState(
            @Parameter(description = "조회 기준 (yyyy.MM.dd).", example = "2025.09.07")
            @RequestParam String calendarDate
    ) {
        return BaseResponse.of(SuccessCode.OK, monitoringService.getCalendarCareState(calendarDate));
    }
}