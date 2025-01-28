package com.wrkr.tickety.domains.ticket.exception;

import com.wrkr.tickety.global.response.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CategoryErrorCode implements BaseErrorCode {

    CATEGORY_FIELD_CANNOT_NULL(HttpStatus.BAD_REQUEST, "CATEGORY_001", "카테고리 이름 혹은 순서 기입은 필수입니다."),
    CATEGORY_NOT_EXIST(HttpStatus.NOT_FOUND, "CATEGORY_401", "카테고리를 찾을 수 없습니다."),
    CATEGORY_ALREADY_EXIST(HttpStatus.CONFLICT, "CATEGORY_901", "이미 존재하는 이름의 카테고리입니다."),
    CATEGORY_ALREADY_DELETED(HttpStatus.CONFLICT,"CATEGORY_902" , "이미 삭제된 카테고리입니다." );

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
