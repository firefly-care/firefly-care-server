package org.farm.fireflyserver.domain.account.web;

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
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/login")
    public BaseResponse<?> login(@RequestBody LoginDto loginDto) {
        TokenDto token = accountService.login(loginDto);
        return BaseResponse.of(SuccessCode.OK, token);
    }
}
