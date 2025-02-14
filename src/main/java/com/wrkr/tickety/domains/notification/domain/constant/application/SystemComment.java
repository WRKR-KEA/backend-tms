package com.wrkr.tickety.domains.notification.domain.constant.application;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SystemComment {
    TICKET_DELEGATE("담당자가 변경되었습니다. (담당자: %s)"),
    TICKET_APPROVE("티켓이 승인되었습니다. (담당자: %s)"),
    ;

    private final String template;

    public String format(Object... args) {
        return String.format(template, args);
    }
}
