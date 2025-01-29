package com.wrkr.tickety.domains.ticket.exception;

import com.wrkr.tickety.global.response.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TemplateErrorCode implements BaseErrorCode {

    TEMPLATE_CANNOT_DO_FOR_SUBCATEGORY_OR_DELETED(HttpStatus.BAD_REQUEST, "TEMPLATE_001", "2차 카테고리 혹은 삭제된 카테고리입니다."),
    TEMPLATE_NOT_EXISTS(HttpStatus.NOT_FOUND, "TEMPLATE_401", "템플릿이 존재하지 않습니다."),
    TEMPLATE_ALREADY_EXISTS(HttpStatus.CONFLICT, "TEMPLATE_901", "템플릿이 이미 존재합니다." );

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
