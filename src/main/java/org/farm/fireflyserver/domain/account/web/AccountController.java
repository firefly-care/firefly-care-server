package org.farm.fireflyserver.domain.account.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.response.BaseResponse;
import org.farm.fireflyserver.common.response.SuccessCode;
import org.farm.fireflyserver.domain.account.service.AccountService;
import org.farm.fireflyserver.domain.account.web.dto.LoginDto;
import org.farm.fireflyserver.domain.account.web.dto.TokenDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Tag(name = "Account", description = "계정 관련 API")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "로그인", description = "사용자 로그인 API(예시 값으로 테스트 가능)")
    @PostMapping("/login")
    public BaseResponse<?> login(@RequestBody LoginDto loginDto) {
        TokenDto token = accountService.login(loginDto);
        return BaseResponse.of(SuccessCode.OK, token);
    }
}
