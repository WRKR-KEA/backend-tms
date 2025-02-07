package com.wrkr.tickety.domains.notification.domain.model;

import com.wrkr.tickety.domains.notification.domain.constant.tickety.NotificationType;
import com.wrkr.tickety.global.model.BaseTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTime {

    private Long notificationId;
    private Long receiverId;
    private NotificationType type;
    private String content;
    private Boolean isRead;

    @Builder
    public Notification(Long notificationId, Long receiverId, NotificationType type, String content, Boolean isRead) {
        this.notificationId = notificationId;
        this.receiverId = receiverId;
        this.type = type;
        this.content = content;
        this.isRead = isRead;
    }
}
