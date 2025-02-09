package com.wrkr.tickety.domains.ticket.exception;

import com.wrkr.tickety.global.response.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements BaseErrorCode {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_401", "코멘트를 찾을 수 없습니다."),

    COMMENT_CONFLICT(HttpStatus.CONFLICT, "COMMENT_901", "해당 상태의 티켓에 코멘트를 달 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
