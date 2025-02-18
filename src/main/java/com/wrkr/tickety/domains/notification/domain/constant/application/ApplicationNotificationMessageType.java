package com.wrkr.tickety.domains.notification.domain.constant.application;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApplicationNotificationMessageType {

    TICKET_DELEGATE_MESSAGE_TO_USER("티켓(%s)의 담당자가 변경되었습니다.\n담당자: %s"),
    TICKET_DELEGATE_MESSAGE_TO_NEW_MANAGER("티켓(%s)의 담당자로 지정되었습니다.\n이전 담당자: %s"),
    TICKET_APPROVED("티켓(%s)이 승인되었습니다.\n담당자: %s"),
    TICKET_REJECT("티켓(%s)이 반려되었습니다."),
    TICKET_FINISHED("티켓(%s)이 완료되었습니다."),

    COMMENT_UPDATE("티켓(%s)에 대한 코멘트가 작성되었습니다."),

    REMIND_TICKET("티켓(%s)에 대한 재확인 요청이 있습니다."),
    ;

    private final String message;

    public String format(Object... args) {
        return String.format(message, args);
    }
}
