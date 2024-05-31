package com.playkuround.demo.domain.target.exception;

import com.playkuround.demo.global.error.ErrorCode;
import com.playkuround.demo.global.error.exception.NotFoundException;

public class TargetNotFoundException extends NotFoundException {

    public TargetNotFoundException() {
        super(ErrorCode.TARGET_NOT_FOUND);
    }
}
