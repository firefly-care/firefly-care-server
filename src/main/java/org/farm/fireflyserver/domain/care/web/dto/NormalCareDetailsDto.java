package org.farm.fireflyserver.domain.care.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.care.persistence.entity.CareAnswer;

@Getter
@RequiredArgsConstructor
public class NormalCareDetailsDto implements CareDetailsDto {
    private final CareAnswer health;
    private final CareAnswer eating;
    private final CareAnswer cognition;
    private final CareAnswer communication;
}