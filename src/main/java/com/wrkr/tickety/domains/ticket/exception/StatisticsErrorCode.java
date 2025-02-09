package com.wrkr.tickety.domains.ticket.exception;

import com.wrkr.tickety.global.response.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StatisticsErrorCode implements BaseErrorCode {
    ILLEGAL_STATISTICS_OPTION(HttpStatus.BAD_REQUEST, "STATISTICS_400", "지원되지 않는 통계 옵션입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
