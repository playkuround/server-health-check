package com.playkuround.demo.global.error.exception;


import com.playkuround.demo.global.error.ErrorCode;

public class AuthenticationException extends BusinessException {

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

}
