package com.wrkr.tickety.domains.ticket.persistence.adapter;

import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketPreResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import com.wrkr.tickety.domains.ticket.persistence.mapper.TicketPersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.repository.TicketRepository;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TicketPersistenceAdapter {

    private final TicketRepository ticketRepository;
    private final TicketPersistenceMapper ticketPersistenceMapper;

    public Ticket save(final Ticket ticket) {
        TicketEntity ticketEntity = this.ticketPersistenceMapper.toEntity(ticket);
        TicketEntity savedEntity = this.ticketRepository.save(ticketEntity);
        return this.ticketPersistenceMapper.toDomain(savedEntity);
    }

    public Page<Ticket> findAllByUserId(final Long userId, final ApplicationPageRequest pageRequest) {
        Pageable pageable = pageRequest.toPageableNoSort();
        return ticketRepository.findAllByUserId(userId, pageable)
            .map(this.ticketPersistenceMapper::toDomain);
    }

    public Ticket findById(final Long ticketId) {
        final TicketEntity ticketEntity = this.ticketRepository.findById(ticketId)
            .orElseThrow(() -> ApplicationException.from(TicketErrorCode.TICKET_NOT_FOUND));
        return this.ticketPersistenceMapper.toDomain(ticketEntity);
    }

    public Page<Ticket> findAll(final String query, final TicketStatus status, final LocalDate startDate, final LocalDate endDate,
        final ApplicationPageRequest pageRequest) {
        Page<TicketEntity> ticketEntityPage = ticketRepository.getAll(query, status, startDate, endDate, pageRequest);
        return ticketEntityPage.map(ticketPersistenceMapper::toDomain);
    }

    public List<DepartmentTicketPreResponse> findAllTicketsNoPaging(
        final String query, final TicketStatus status, final LocalDate startDate, final LocalDate endDate
    ) {
        return ticketRepository.getAllTicketsNoPaging(query, status, startDate, endDate);
    }

    public Long findTicketCountByCategoryAndDateRange(final Long categoryId, final LocalDateTime start, final LocalDateTime end) {
        return ticketRepository.findTicketCountByCategoryAndDateRange(categoryId, start, end);
    }

    public Page<Ticket> findAllByManagerFilter(final Long managerId, final ApplicationPageRequest pageRequest, final TicketStatus status, final String query) {
        return ticketRepository.findByManagerFilters(managerId, status, pageRequest, query).map(this.ticketPersistenceMapper::toDomain);
    }

    public List<Ticket> findAllByManagerAndIsPinned(Long managerId) {
        List<TicketEntity> ticketEntities = ticketRepository.findAllByManager_memberIdAndIsPinnedTrue(managerId);
        return ticketEntities.stream()
            .map(ticketPersistenceMapper::toDomain).toList();
    }

    public List<Ticket> findRequests() {
        List<TicketEntity> ticketEntities = ticketRepository.findTop10ByStatusOrderByCreatedAtDesc(TicketStatus.REQUEST);
        return ticketEntities.stream()
            .map(ticketPersistenceMapper::toDomain).toList();

    }

    public List<Ticket> findRecentsByUserId(Long userId) {
        List<TicketEntity> ticketEntities = ticketRepository.findTop10ByUser_memberIdOrderByUpdatedAtDesc(userId);
        return ticketEntities.stream()
            .map(ticketPersistenceMapper::toDomain).toList();
    }

    public List<Ticket> findManagersInProgressTickets(List<Long> managerIds) {
        List<TicketEntity> ticketEntities = ticketRepository.findByManager_memberIdInAndStatus(managerIds, TicketStatus.IN_PROGRESS);
        return ticketEntities.stream()
            .map(ticketPersistenceMapper::toDomain).toList();
    }

    public Long countByCreateAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return ticketRepository.countByCreatedAtBetween(startDate, endDate);
    }

    public Long countByManagerAndIsPinned(Long managerId) {
        return ticketRepository.countByManager_memberIdAndIsPinnedTrue(managerId);
    }

    public boolean isPinTicket(Long ticketId) {
        return ticketRepository.existsByticketIdAndIsPinnedTrue(ticketId);
    }

    public Page<Ticket> findAllByUserIdAndStatus(Long userId, TicketStatus status, ApplicationPageRequest pageRequest) {
        Pageable pageable = pageRequest.toPageableNoSort();
        return ticketRepository.findAllByUserIdAndStatus(userId, status, pageable)
            .map(this.ticketPersistenceMapper::toDomain);
    }

    public String findLastSequence(String today, Category childCategory) {
        return ticketRepository.findLastSequence(today, childCategory.getAbbreviation(), childCategory.getParent().getAbbreviation());
    }
}
