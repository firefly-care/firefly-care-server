package com.example.comus.global.error.exception;

import org.farm.fireflyserver.common.exception.BusinessException;
import org.farm.fireflyserver.common.response.ErrorCode;

public class ConflictException extends BusinessException {
    public ConflictException() {
        super(ErrorCode.CONFLICT);
    }

    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }
}