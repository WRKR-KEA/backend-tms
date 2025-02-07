package com.wrkr.tickety.domains.notification.application.mapper;

import com.wrkr.tickety.domains.notification.domain.constant.tickety.NotificationType;
import com.wrkr.tickety.domains.notification.domain.model.Notification;

public class NotificationMapper {

    private NotificationMapper() {
        throw new IllegalArgumentException();
    }

    public static Notification toNotification(Long receiverId, NotificationType type, String content) {
        return Notification.builder()
            .receiverId(receiverId)
            .type(type)
            .content(content)
            .build();
    }
}
