package com.wrkr.tickety.domains.ticket.exception;

import com.wrkr.tickety.global.response.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TemplateErrorCode implements BaseErrorCode {

    TEMPLATE_NOT_EXIST(HttpStatus.NOT_FOUND, "TEMPLATE_401", "템플릿이 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
