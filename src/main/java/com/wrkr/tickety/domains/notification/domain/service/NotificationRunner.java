package com.wrkr.tickety.domains.notification.domain.service;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.service.application.SendApplicationNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.kakaowork.KakaoworkNotificationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRunner {

    private enum NotificationSource {
        AGIT, EMAIL, SERVICE, KAKAOWORK;
    }

    private static final Map<Class<?>, NotificationSource> ACTION_TO_TYPE_MAP = Map.of(
        SendAgitNotificationService.class, NotificationSource.AGIT,
        SendEmailNotificationService.class, NotificationSource.EMAIL,
        SendApplicationNotificationService.class, NotificationSource.SERVICE,
        KakaoworkNotificationService.class, NotificationSource.KAKAOWORK
    );

    @Async
    public void run(Member member, Runnable action) {
        NotificationSource source = detectNotificationType(action);
        if (!isNotificationEnabled(source, member)) {
            return;
        }
        try {
            action.run();
        } catch (Exception e) {
            log.warn("Notification Failed. [source={}, memberId={}] [{}]", source, member.getMemberId(), e.getMessage());
        }
    }

    private boolean isNotificationEnabled(NotificationSource source, Member member) {
        return switch (source) {
            case AGIT -> member.getAgitNotification();
            case EMAIL -> member.getEmailNotification();
            case SERVICE -> member.getServiceNotification();
            case KAKAOWORK -> member.getKakaoworkNotification();
        };
    }

    private NotificationSource detectNotificationType(Runnable action) {
        Class<?> declaringClass = action.getClass().getEnclosingClass();
        return ACTION_TO_TYPE_MAP.get(declaringClass);
    }
}
