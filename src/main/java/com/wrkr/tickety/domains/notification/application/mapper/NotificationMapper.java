package com.wrkr.tickety.domains.notification.application.mapper;

import static com.wrkr.tickety.global.utils.date.NotificationTimeFormatter.formatRelativeTime;

import com.wrkr.tickety.domains.notification.application.dto.ApplicationNotificationResponse;
import com.wrkr.tickety.domains.notification.domain.constant.tickety.NotificationType;
import com.wrkr.tickety.domains.notification.domain.model.Notification;
import com.wrkr.tickety.global.utils.PkCrypto;

public class NotificationMapper {

    private NotificationMapper() {
        throw new IllegalArgumentException();
    }

    public static Notification toNotification(Long memberId, String profileImage, NotificationType type, String content) {
        return Notification.builder()
            .memberId(memberId)
            .profileImage(profileImage)
            .type(type)
            .content(content)
            .build();
    }

    public static ApplicationNotificationResponse toApplicationNotificationResponse(Notification notification) {
        return ApplicationNotificationResponse.builder()
            .notificationId(PkCrypto.encrypt(notification.getNotificationId()))
            .memberId(PkCrypto.encrypt(notification.getMemberId()))
            .profileImage(notification.getProfileImage())
            .content(notification.getContent())
            .type(notification.getType())
            .isRead(notification.getIsRead())
            .timeAgo(formatRelativeTime(notification.getCreatedAt()))
            .createdAt(notification.getCreatedAt())
            .build();
    }
}
