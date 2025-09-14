package org.farm.fireflyserver.domain.account.web.dto;

public record TokenDto(
        String accessToken,
        String accountName
) {
}
