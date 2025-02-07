package com.wrkr.tickety.domains.notification.persistence.repository;

import com.wrkr.tickety.domains.notification.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

}
