package com.wrkr.tickety.domains.notification.domain.constant.application;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Remind {

    REMIND_TICKET("티켓(%s)에 대한 재확인 요청이 있습니다."),
    ;

    private final String message;

    public String format(Object... args) {
        return String.format(message, args);
    }
}
