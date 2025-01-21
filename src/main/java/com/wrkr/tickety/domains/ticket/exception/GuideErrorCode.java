package com.wrkr.tickety.domains.ticket.exception;

import com.wrkr.tickety.global.response.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GuideErrorCode implements BaseErrorCode {
    GuideNotExist(HttpStatus.NOT_FOUND, "Guide_001", "도움말이 존재하지 않습니다."),
    GuideAlreadyExist(HttpStatus.BAD_REQUEST, "Guide_002", "도움말이 이미 존재합니다.");

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
