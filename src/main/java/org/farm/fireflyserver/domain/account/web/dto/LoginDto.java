package org.farm.fireflyserver.domain.account.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginDto(
        @Schema(example = "manager_kim")
        String id,
        @Schema(example = "password123")
        String password
) {
}
