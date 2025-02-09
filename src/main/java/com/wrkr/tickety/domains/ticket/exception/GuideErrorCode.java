package com.wrkr.tickety.domains.ticket.exception;

import com.wrkr.tickety.global.response.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GuideErrorCode implements BaseErrorCode {
    GUIDE_NOT_EXIST(HttpStatus.NOT_FOUND, "GUIDE_001", "도움말이 존재하지 않습니다."),
    GUIDE_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "GUIDE_002", "도움말이 이미 존재합니다.");

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
