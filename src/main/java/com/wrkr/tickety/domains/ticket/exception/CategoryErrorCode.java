package com.wrkr.tickety.domains.ticket.exception;

import com.wrkr.tickety.global.response.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CategoryErrorCode implements BaseErrorCode {

    CATEGORY_NOT_EXISTS(HttpStatus.NOT_FOUND, "CATEGORY_401", "카테고리를 찾을 수 없습니다."),
    CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "CATEGORY_901", "카테고리 이름이 이미 존재합니다."),
    CATEGORY_ABBREVIATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "CATEGORY_902", "카테고리의 약어가 이미 존재합니다."),
    CATEGORY_SEQUENCE_ALREADY_EXISTS(HttpStatus.CONFLICT, "CATEGORY_903", "카테고리 순서가 이미 존재합니다.");

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
