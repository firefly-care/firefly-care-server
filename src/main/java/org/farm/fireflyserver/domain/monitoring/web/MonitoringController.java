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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/monitoring")
@Tag(name = "Monitoring", description = "모니터링/대시보드 관련 API")
public class MonitoringController {

    private final MonitoringService monitoringService;

    @GetMapping("/main")
    @Operation(summary = "메인 홈 모니터링 정보 조회",
            description = "메인 홈 모니터링 정보 조회(레이아웃별로 구성된 정보 반환) + \n"+
                    "지역별 대상자 상태 현황은 필터링 아직 적용 전이라 지역별 대상자 상태 현황을 한 번에 반환합니다!(이후 적용 예정)")
    public BaseResponse<?> getMainHome(){
        MainHomeDto mainHome = monitoringService.getMainHome();
        return BaseResponse.of(SuccessCode.OK, mainHome);
    }
}
