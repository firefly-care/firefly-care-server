package org.farm.fireflyserver.common.util;

import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping
    BaseResponse<String> healthChecker(){
        String message = "Hello Firefly Server!";
        return BaseResponse.of(SuccessCode.OK, message);
    }

}
