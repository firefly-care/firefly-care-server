package org.farm.fireflyserver.domain.care;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.farm.fireflyserver.common.exception.InvalidValueException;
import org.farm.fireflyserver.common.response.ErrorCode;

import java.util.stream.Stream;

public enum Type {
    CALL,
    VISIT,
    EMERGENCY;

    @JsonCreator
    public static Type from(String type) {
        return Stream.of(Type.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ErrorCode.INVALID_INPUT_VALUE));
    }
}