package org.farm.fireflyserver.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@JsonPropertyOrder({"status", "message", "data"})
public class BaseResponse<T> {
    private final int status;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> BaseResponse<T> of(SuccessCode successCode, T data) {
        return BaseResponse.<T>builder()
                .status(successCode.getHttpStatus().value())
                .message(successCode.getMessage())
                .data(data)
                .build();
    }

    public static BaseResponse<?> of(ErrorCode errorCode) {
        return BaseResponse.builder()
                .status(errorCode.getHttpStatus().value())
                .message(errorCode.getMessage())
                .build();
    }
}
