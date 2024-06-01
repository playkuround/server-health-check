package com.playkuround.demo.domain.email.exception;

import com.playkuround.demo.global.error.ErrorCode;
import com.playkuround.demo.global.error.exception.BusinessException;

public class EmailDuplicationHostException extends BusinessException {

    public EmailDuplicationHostException() {
        super(ErrorCode.EMAIL_DUPLICATE);
    }
}
