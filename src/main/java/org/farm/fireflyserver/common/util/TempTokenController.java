package org.farm.fireflyserver.common.util;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.config.jwt.JwtProvider;
import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RestController
@Tag(name = "임시 토큰 발급", description = "임시 토큰 발급 API")
public class TempTokenController {

    private final JwtProvider jwtProvider;

    @Operation(summary = "임시 토큰 발급")
    @PostMapping("/token/{id}")
    BaseResponse<String> getTempToken(@PathVariable Long id) {
        String token = jwtProvider.getIssueToken(id, true);
        return BaseResponse.of(SuccessCode.OK, token);
    }
}
