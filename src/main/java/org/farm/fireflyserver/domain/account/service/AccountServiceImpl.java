package org.farm.fireflyserver.domain.account.service;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.config.jwt.JwtProvider;
import org.farm.fireflyserver.common.exception.EntityNotFoundException;
import org.farm.fireflyserver.domain.account.persistence.AccountRepository;
import org.farm.fireflyserver.domain.account.persistence.entity.Account;
import org.farm.fireflyserver.domain.account.web.dto.LoginDto;
import org.farm.fireflyserver.domain.account.web.dto.TokenDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.farm.fireflyserver.common.response.ErrorCode.ACCOUNT_ID_NOT_FOUND;
import static org.farm.fireflyserver.common.response.ErrorCode.PASSWORD_NOT_FOUND;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;


    @Override
    public TokenDto login(LoginDto loginDto) {
        String id = loginDto.id();
        String password = loginDto.password();

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ACCOUNT_ID_NOT_FOUND));

        if (!passwordEncoder.matches(password, account.getPwd())) {
            throw new EntityNotFoundException(PASSWORD_NOT_FOUND);
        }

        return new TokenDto(getAccessToken(account), account.getName());

    }

    private String getAccessToken(Account account) {
        return jwtProvider.getIssueToken(account.getAccountId(), true);
    }
}
