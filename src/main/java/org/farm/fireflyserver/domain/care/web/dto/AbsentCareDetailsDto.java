package org.farm.fireflyserver.domain.care.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AbsentCareDetailsDto implements CareDetailsDto {
    private final Boolean first;
    private final Boolean second;
    private final Boolean third;
    private final Boolean fourth;
}

