package com.wrkr.tickety.domains.notification.domain.service.application;

import com.wrkr.tickety.domains.notification.domain.model.Notification;
import com.wrkr.tickety.domains.notification.persistence.adapter.NotificationPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSaveService {

    private final NotificationPersistenceAdapter notificationPersistenceAdapter;

    public Notification save(Notification notification) {
        return notificationPersistenceAdapter.save(notification);
    }
}
