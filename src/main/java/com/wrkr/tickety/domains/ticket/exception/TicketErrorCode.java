package com.wrkr.tickety.domains.ticket.exception;

import com.wrkr.tickety.global.response.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TicketErrorCode implements BaseErrorCode {

    TICKET_NOT_FOUND(HttpStatus.NOT_FOUND, "TICKET_001", "티켓을 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "TICKET_002", "티켓에 접근할 권한이 없습니다."),
    TICKET_NOT_BELONG_TO_USER(HttpStatus.FORBIDDEN, "TICKET_003", "티켓이 사용자에게 속해있지 않습니다."),
    TICKET_NOT_REQUEST_STATUS(HttpStatus.BAD_REQUEST, "TICKET_004", "요청 상태가 아닌 티켓은 취소할 수 없습니다."),
    TICKET_MANAGER_NOT_MATCH(HttpStatus.BAD_REQUEST, "TICKET_005", "해당 티켓의 담당자가 아닙니다."),
    TICKET_MANAGER_NOT_FOUND(HttpStatus.BAD_REQUEST, "TICKET_006", "해당 티켓의 담당자를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
