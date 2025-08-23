package org.farm.fireflyserver.domain.care;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.common.exception.InvalidValueException;
import org.farm.fireflyserver.common.response.ErrorCode;

import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public enum Type {
    CALL("전화돌봄"),
    VISIT("방문돌봄"),
    EMERGENCY("긴급출동");

    private final String desc;

    @JsonCreator
    public static Type from(String type) {
        return Stream.of(Type.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ErrorCode.INVALID_INPUT_VALUE));
    }
}