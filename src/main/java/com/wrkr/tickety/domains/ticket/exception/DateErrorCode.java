package com.wrkr.tickety.domains.ticket.exception;

import com.wrkr.tickety.global.response.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DateErrorCode implements BaseErrorCode {
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "DATE_001", "날짜 형식이 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
