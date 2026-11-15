package com.group.security.global.exception;

public class UnauthorizedException extends CustomException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
