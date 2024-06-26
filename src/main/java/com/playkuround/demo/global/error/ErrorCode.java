package com.playkuround.demo.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    NOT_FOUND(HttpStatus.BAD_REQUEST, "C001", "Not Found resource"),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C003", "잘못된 HTTP 메서드입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C004", "권한이 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C005", "서버 내부에서 에러가 발생하였습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "C006", "Bad Request"),
    FAIL_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "C007", "인증에 실패했습니다."),


    // Target
    TARGET_NOT_FOUND(HttpStatus.BAD_REQUEST, "T001", "해당 Target을 찾을 수 없습니다."),
    TARGET_DUPLICATE_HOST(HttpStatus.BAD_REQUEST, "T002", "중복된 host 입니다."),

    // Email
    EMAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "E002", "중복된 이메일 입니다."),
    EMAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "E002", "이메일 전송에 실패하였습니다."),

    ;
    private final HttpStatus status;
    private final String code;
    private final String message;

}
