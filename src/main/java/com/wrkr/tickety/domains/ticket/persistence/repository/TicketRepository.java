package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.persistence.entity.CategoryEntity;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import java.time.LocalDateTime;
import java.util.List;
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

    List<TicketEntity> findAllByManager_memberIdAndIsPinnedTrue(Long managerId);

    List<TicketEntity> findTop10ByStatusOrderByCreatedAtDesc(TicketStatus status);

    List<TicketEntity> findTop10ByUser_memberIdOrderByUpdatedAtDesc(Long userId);

    List<TicketEntity> findByManager_memberIdInAndStatus(List<Long> managerIds, TicketStatus ticketStatus);

    Long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    Long countByManager_memberIdAndIsPinnedTrue(Long managerId);

    boolean existsByticketIdAndIsPinnedTrue(Long ticketId);

    @Query("SELECT t FROM TicketEntity t WHERE t.user.memberId = :userId AND t.status = :status")
    Page<TicketEntity> findAllByUserIdAndStatus(@Param("userId") Long userId, @Param("status") TicketStatus status, Pageable pageable);

    @Query(
        "SELECT MAX(CAST(RIGHT(t.serialNumber, 2) AS int)) " +
            "FROM TicketEntity t " +
            "WHERE t.serialNumber LIKE CONCAT('#', :today, :parentCategory, :childCategory, '%')"
    )
    String findLastSequence(
        @Param("today") String today,
        @Param("childCategory") String childCategory,
        @Param("parentCategory") String parentCategory
    );

    List<TicketEntity> category(CategoryEntity category);
}
