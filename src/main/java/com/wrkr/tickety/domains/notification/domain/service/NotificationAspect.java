package com.wrkr.tickety.domains.notification.domain.service;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.service.application.SendApplicationNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.kakaowork.KakaoworkNotificationService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class NotificationAspect {

    private enum NotificationSource {
        AGIT, EMAIL, SERVICE, KAKAOWORK;
    }

    private static final Map<Class<?>, NotificationSource> ACTION_TO_TYPE_MAP = Map.of(
        SendAgitNotificationService.class, NotificationSource.AGIT,
        SendEmailNotificationService.class, NotificationSource.EMAIL,
        SendApplicationNotificationService.class, NotificationSource.SERVICE,
        KakaoworkNotificationService.class, NotificationSource.KAKAOWORK
    );

    private boolean isNotificationEnabled(NotificationSource source, Member member) {
        if (member.getMemberId() == null) {
            return true;
        } else {
            return switch (source) {
                case AGIT -> member.getAgitNotification();
                case EMAIL -> member.getEmailNotification();
                case SERVICE -> member.getServiceNotification();
                case KAKAOWORK -> member.getKakaoworkNotification();
            };
        }
    }

    private NotificationSource detectNotificationType(ProceedingJoinPoint joinPoint) {
        Class<?> declaringClass = joinPoint.getSignature().getDeclaringType();
        return ACTION_TO_TYPE_MAP.get(declaringClass);
    }

    @Pointcut("execution(* com.wrkr.tickety.domains.notification.domain.service..*NotificationService.*(..))")
    public void notificationService() {
    }

    @Around("notificationService()")
    public Object notificationProcessor(ProceedingJoinPoint joinPoint) throws Throwable {
        NotificationSource source = detectNotificationType(joinPoint);
        Member receiver = joinPoint.getArgs()[0] instanceof Member
            ? (Member) joinPoint.getArgs()[0]
            : Member.builder().memberId(null).build();

        Object result = null;
        if (!isNotificationEnabled(source, receiver)) {
            return null;
        }
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            log.warn("Notification Failed. [source={}, memberId={}] [{}]", source, receiver, e.getMessage());
        }
        return result;
    }
}
