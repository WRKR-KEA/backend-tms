package com.wrkr.tickety.domains.notification.domain.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AgitCommentNotificationMessage {
    COMMENT_UPDATE("티켓 %s 에 대한 코멘트가 작성되었습니다."),
    ;

    private final String template;

    /**
     * 포맷의 플레이스 홀더에 인자를 넣어서 메시지를 생성한다.
     */
    public String format(Object... args) {
        return String.format(template, args);
    }
}
