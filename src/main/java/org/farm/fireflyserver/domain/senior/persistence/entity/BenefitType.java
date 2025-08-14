package org.farm.fireflyserver.domain.senior.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BenefitType {
    BASIC_LIVELIHOOD("기초수급자"),
    BASIC_PENSION("기초연금"),
    NEAR_POOR("차상위"),
    OTHER("기타");

    private final String description;
}
