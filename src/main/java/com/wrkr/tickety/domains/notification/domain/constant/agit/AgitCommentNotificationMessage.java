package com.wrkr.tickety.domains.notification.domain.constant.agit;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AgitCommentNotificationMessage {
    COMMENT_UPDATE("티켓(%s)에 대한 코멘트가 작성되었습니다."),
    ;

    private final String template;

    public String format(Object... args) {
        return String.format(template, args);
    }
}
