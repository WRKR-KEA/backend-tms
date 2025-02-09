package com.wrkr.tickety.domains.ticket.exception;

import com.wrkr.tickety.global.response.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CategoryErrorCode implements BaseErrorCode {

    CATEGORY_NOT_EXISTS(HttpStatus.NOT_FOUND, "CATEGORY_401", "카테고리를 찾을 수 없습니다."),
    CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "CATEGORY_901", "이미 존재하는 이름의 카테고리입니다.");

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
