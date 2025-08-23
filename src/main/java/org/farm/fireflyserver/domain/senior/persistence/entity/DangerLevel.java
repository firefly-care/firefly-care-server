package org.farm.fireflyserver.domain.senior.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DangerLevel {
    NORMAL("정상"),
    ATTENTION("관심"),
    CAUTION("주의"),
    DANGER("위험");

    private final String label;

    public static DangerLevel from(String value) {
        if (value == null) return NORMAL;
        for (DangerLevel dl : values()) {
            if (dl.label.equals(value)) return dl;
        }
        return NORMAL;
    }
}
