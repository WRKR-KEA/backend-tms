package com.wrkr.tickety.domains.ticket.exception;

import com.wrkr.tickety.global.response.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TicketErrorCode implements BaseErrorCode {

    TICKET_NOT_REQUEST_STATUS(HttpStatus.BAD_REQUEST, "TICKET_001", "요청 상태가 아닌 티켓은 취소할 수 없습니다."),
    TICKET_MANAGER_NOT_FOUND(HttpStatus.BAD_REQUEST, "TICKET_002", "티켓의 담당자가 존재하지 않습니다."),
    TICKET_STATUS_NOT_IN_PROGRESS(HttpStatus.BAD_REQUEST, "TICKET_003", "진행 중인 티켓이 아닙니다."),
    TICKET_NOT_APPROVABLE(HttpStatus.BAD_REQUEST, "TICKET_004", "티켓 승인이 불가능한 상태입니다."),
    TICKET_NOT_REJECTABLE(HttpStatus.BAD_REQUEST, "TICKET_005", "티켓 반려가 불가능한 상태입니다."),
    TICKET_NOT_COMPLETABLE(HttpStatus.BAD_REQUEST, "TICKET_006", "티켓 완료가 불가능한 상태입니다."),

    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "TICKET_301", "티켓에 접근할 권한이 없습니다."),
    TICKET_NOT_BELONG_TO_USER(HttpStatus.FORBIDDEN, "TICKET_302", "티켓이 사용자에게 속해있지 않습니다."),
    TICKET_MANAGER_NOT_MATCH(HttpStatus.FORBIDDEN, "TICKET_303", "해당 티켓의 담당자가 아닙니다."),

    TICKET_NOT_FOUND(HttpStatus.NOT_FOUND, "TICKET_401", "티켓을 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String customCode;
    private final String message;
}
