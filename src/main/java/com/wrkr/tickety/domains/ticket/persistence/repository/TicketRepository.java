package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<TicketEntity, Long>, TicketQueryDslRepository {

    @Query("SELECT t FROM TicketEntity t WHERE t.user.memberId = :userId")
    Page<TicketEntity> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(t) FROM TicketEntity t WHERE t.category.categoryId = :categoryId AND t.createdAt >= :date AND t.createdAt < :end")
    Long findTicketCountByCategoryAndDateRange(Long categoryId, LocalDateTime date, LocalDateTime end);
}
