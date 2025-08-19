package org.farm.fireflyserver.domain.account.service;

import org.farm.fireflyserver.domain.account.web.dto.LoginDto;

public interface AccountService {
    void login(LoginDto loginDto);
}
