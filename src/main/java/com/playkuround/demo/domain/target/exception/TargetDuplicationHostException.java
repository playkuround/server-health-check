package com.playkuround.demo.domain.target.exception;

import com.playkuround.demo.global.error.ErrorCode;
import com.playkuround.demo.global.error.exception.BusinessException;

public class TargetDuplicationHostException extends BusinessException {

    public TargetDuplicationHostException() {
        super(ErrorCode.TARGET_DUPLICATE_HOST);
    }
}
