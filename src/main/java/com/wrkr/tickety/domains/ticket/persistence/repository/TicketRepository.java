package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<TicketEntity, Long>, TicketQueryDslRepository {

    @Query("SELECT t FROM TicketEntity t WHERE t.user.memberId = :userId")
    Page<TicketEntity> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
            SELECT t
            FROM TicketEntity t
            WHERE t.manager.memberId = :managerId
              AND (:status IS NULL OR t.status = :status)
              AND (:search IS NULL OR t.serialNumber LIKE %:search% OR t.content LIKE %:search%)
            ORDER BY  t.isPinned
        """)
    Page<TicketEntity> findByManagerFilters(
        @Param("managerId") Long managerId,
        @Param("status") TicketStatus status,
        Pageable pageable,
        @Param("search") String search
    );
}
