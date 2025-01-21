package com.wrkr.tickety.domains.ticket.exception;

import com.wrkr.tickety.global.response.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements BaseErrorCode {

    TICKET_STATUS_INVALID_FOR_COMMENT(HttpStatus.CONFLICT, "COMMENT_901", "해당 상태의 티켓에 코멘트를 달 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
