package com.wrkr.tickety.domains.alarm.domain.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AgitTicketNotificationMessageType {
    TICKET_APPROVED("티켓 %s 이 승인되었습니다."),
    TICKET_REJECT("티켓 %s 이 반려되었습니다."),
    TICKET_FINISHED("티켓 %s 이 완료되었습니다."),
    ;

    private final String template;

    /**
     * 포맷의 플레이스 홀더에 인자를 넣어서 메시지를 생성한다.
     */
    public String format(Object... args) {
        return String.format(template, args);
    }
}
