package com.wrkr.tickety.domains.notification.persistence.adapter;

import com.wrkr.tickety.domains.notification.domain.model.Notification;
import com.wrkr.tickety.domains.notification.persistence.entity.NotificationEntity;
import com.wrkr.tickety.domains.notification.persistence.mapper.NotificationPersistenceMapper;
import com.wrkr.tickety.domains.notification.persistence.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationPersistenceAdapter {

    private final NotificationRepository notificationRepository;
    private final NotificationPersistenceMapper notificationPersistenceMapper;

    public Notification save(final Notification notification) {
        NotificationEntity ticketEntity = this.notificationPersistenceMapper.toEntity(notification);
        NotificationEntity savedEntity = this.notificationRepository.save(ticketEntity);
        return this.notificationPersistenceMapper.toDomain(savedEntity);
    }
}
