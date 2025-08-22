package org.farm.fireflyserver.domain.account.service;

import org.farm.fireflyserver.domain.account.web.dto.LoginDto;
import org.farm.fireflyserver.domain.account.web.dto.TokenDto;

public interface AccountService {
    TokenDto login(LoginDto loginDto);
}
