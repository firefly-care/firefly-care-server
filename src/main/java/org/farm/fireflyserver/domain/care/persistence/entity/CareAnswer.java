package org.farm.fireflyserver.domain.care.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CareAnswer {
    NORMAL("정상"),
    WARN("주의"),
    EMER("위급");
    private final String desc;
}