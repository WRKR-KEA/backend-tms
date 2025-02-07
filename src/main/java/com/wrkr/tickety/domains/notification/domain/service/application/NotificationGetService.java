package com.wrkr.tickety.domains.notification.domain.service.application;

import com.wrkr.tickety.domains.notification.domain.model.Notification;
import com.wrkr.tickety.domains.notification.persistence.adapter.NotificationPersistenceAdapter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationGetService {

    private final NotificationPersistenceAdapter notificationPersistenceAdapter;

    public List<Notification> getAllNotifications(Long memberId) {
        return notificationPersistenceAdapter.getAllNotificationsByMemberId(memberId);
    }
}
