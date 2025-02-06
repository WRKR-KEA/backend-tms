package com.wrkr.tickety.domains.notification.domain.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AgitTicketDelegateNotificationMessage {
    TICKET_DELEGATE_MESSAGE_TO_USER("티켓(%s)의 담당자가 변경되었습니다. (담당자: %s)"),
    TICKET_DELEGATE_MESSAGE_TO_NEW_MANAGER("이전 담당자 (%s)이 티켓(%s)를 위임했습니다."),
    ;

    private final String template;

    public String format(Object... args) {
        return String.format(template, args);
    }
}
