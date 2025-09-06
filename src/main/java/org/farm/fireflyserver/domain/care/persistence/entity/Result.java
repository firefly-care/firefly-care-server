package org.farm.fireflyserver.domain.care.persistence.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.exception.InvalidValueException;
import org.farm.fireflyserver.common.response.ErrorCode;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Result {
    NORMAL("정상"),
    ABSENT("부재");
    private final String desc;

    @JsonCreator
    public static Result from(String result) {
        return Stream.of(Result.values())
                .filter(r -> r.name().equalsIgnoreCase(result))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ErrorCode.INVALID_INPUT_VALUE));
    }
}
