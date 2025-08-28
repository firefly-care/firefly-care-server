package org.farm.fireflyserver.domain.care.persistence.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.farm.fireflyserver.common.exception.InvalidValueException;
import org.farm.fireflyserver.common.response.ErrorCode;

import java.util.stream.Stream;

public enum Result {
    NORMAL, 
    ABSENT;

    @JsonCreator
    public static Result from(String result) {
        return Stream.of(Result.values())
                .filter(r -> r.name().equalsIgnoreCase(result))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ErrorCode.INVALID_INPUT_VALUE));
    }
}
