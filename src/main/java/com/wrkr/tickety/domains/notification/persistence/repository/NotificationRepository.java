package com.wrkr.tickety.domains.notification.persistence.repository;

import com.wrkr.tickety.domains.notification.persistence.entity.NotificationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findAllByMemberIdOrderByCreatedAtDesc(final Long memberId);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.memberId = :memberId")
    void updateAllIsReadTrueByMemberId(@Param("memberId") Long memberId);

    long countDistinctByMemberIdAndIsRead(Long memberId, boolean isRead);
}
