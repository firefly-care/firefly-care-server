package org.farm.fireflyserver.common.util;

import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<BaseResponse<?>> healthCheck() {
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.OK, "Firefly Server Health Check OK"));
    }
}
