package org.farm.fireflyserver.common.util;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "서버 HealthCheck", description = "Health Check API")
public class HealthCheckController {

    @GetMapping
    BaseResponse<String> healthChecker(){
        String message = "Hello Firefly Server!";
        return BaseResponse.of(SuccessCode.OK, message);
    }

}
