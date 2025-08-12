package org.farm.fireflyserver.common.util;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.config.jwt.JwtProvider;
import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RestController
public class TempTokenController {

    private final JwtProvider jwtProvider;

    @PostMapping("/token/{id}")
    BaseResponse<String> getTempToken(@PathVariable Long id) {
        String token = jwtProvider.getIssueToken(id, true);
        return BaseResponse.of(SuccessCode.OK, token);
    }
}
