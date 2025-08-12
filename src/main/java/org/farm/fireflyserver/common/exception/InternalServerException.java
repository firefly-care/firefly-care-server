package org.farm.fireflyserver.common.exception;


import org.farm.fireflyserver.common.response.ErrorCode;

public class InternalServerException extends BusinessException {
    public InternalServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}