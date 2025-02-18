package com.wrkr.tickety.domains.notification.persistence.adapter;

import com.wrkr.tickety.domains.notification.domain.model.Notification;
import com.wrkr.tickety.domains.notification.persistence.entity.NotificationEntity;
import com.wrkr.tickety.domains.notification.persistence.mapper.NotificationPersistenceMapper;
import com.wrkr.tickety.domains.notification.persistence.repository.NotificationRepository;
import java.util.List;
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

    public List<Notification> getAllNotificationsByMemberId(final Long memberId) {
        List<NotificationEntity> notificationEntities = this.notificationRepository.findAllByMemberId(memberId);
        return notificationEntities.stream()
            .map(this.notificationPersistenceMapper::toDomain)
            .toList();
    }

    public void updateAllIsReadTrueByMemberId(final Long memberId) {
        notificationRepository.updateAllIsReadTrueByMemberId(memberId);
    }

    public long countDistinctByMemberIdAndIsRead(final Long memberId, final Boolean isRead) {
        return this.notificationRepository.countDistinctByMemberIdAndIsRead(memberId, isRead);
    }
}
