package com.playkuround.demo.global.error.exception;

import com.playkuround.demo.global.error.ErrorCode;

public class IllegalException extends BusinessException {

    public IllegalException() {
        super(ErrorCode.BAD_REQUEST);
    }

    public IllegalException(ErrorCode e) {
        super(e);
    }
}
