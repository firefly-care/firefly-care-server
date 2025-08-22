package org.farm.fireflyserver.domain.monitoring.web;

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
public class MonitoringController {

    private final MonitoringService monitoringService;

    @GetMapping("/main")
    public BaseResponse<?> getMainHome(){
        MainHomeDto mainHome = monitoringService.getMainHome();
        return BaseResponse.of(SuccessCode.OK, mainHome);
    }
}
