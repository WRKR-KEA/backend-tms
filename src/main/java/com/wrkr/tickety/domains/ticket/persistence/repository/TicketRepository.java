package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    @Query("SELECT t FROM TicketEntity t WHERE t.user.memberId = :userId")
    Page<TicketEntity> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
                SELECT t
                FROM TicketEntity t
                WHERE t.manager.memberId = :managerId
                  AND (:status IS NULL OR t.status = :status)
                ORDER BY CASE WHEN :isPinned = TRUE THEN t.isPinned END DESC, t.ticketId ASC
            """)
    Page<TicketEntity> findByManagerFilters(
            @Param("managerId") Long managerId,
            @Param("status") TicketStatus status,
            @Param("isPinned") Boolean isPinned,
            Pageable pageable
    );
}
