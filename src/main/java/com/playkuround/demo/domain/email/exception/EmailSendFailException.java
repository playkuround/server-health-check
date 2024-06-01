package com.playkuround.demo.domain.email.exception;

import com.playkuround.demo.global.error.ErrorCode;
import com.playkuround.demo.global.error.exception.BusinessException;

public class EmailSendFailException extends BusinessException {

    public EmailSendFailException() {
        super(ErrorCode.EMAIL_SEND_FAIL);
    }

}
