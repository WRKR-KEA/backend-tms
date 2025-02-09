package com.wrkr.tickety.domains.notification.application.usecase;

import com.wrkr.tickety.domains.notification.application.dto.ApplicationNotificationResponse;
import com.wrkr.tickety.domains.notification.application.mapper.NotificationMapper;
import com.wrkr.tickety.domains.notification.domain.model.Notification;
import com.wrkr.tickety.domains.notification.domain.service.application.NotificationGetService;
import com.wrkr.tickety.domains.notification.domain.service.application.NotificationUpdateService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class ApplicationNotificationGetUseCase {

    private final NotificationGetService notificationGetService;
    private final NotificationUpdateService notificationUpdateService;

    public List<ApplicationNotificationResponse> getAllNotifications(Long memberId) {
        List<Notification> notifications = notificationGetService.getAllNotifications(memberId);
        notificationUpdateService.updateAllIsReadByMemberId(memberId);

        return notifications.stream()
            .map(NotificationMapper::toApplicationNotificationResponse)
            .collect(Collectors.toList());
    }
}
