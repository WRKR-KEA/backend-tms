package com.wrkr.tickety.domains.ticket.domain.service.ticket;

import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketExcelPreResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketPersistenceAdapter;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketGetService {

    private final TicketPersistenceAdapter ticketPersistenceAdapter;

    public Page<Ticket> getTicketsByUserId(Long userId, ApplicationPageRequest pageRequest) {
        return ticketPersistenceAdapter.findAllByUserId(userId, pageRequest);
    }

    public Ticket getTicketByTicketId(Long ticketId) {
        return ticketPersistenceAdapter.findById(ticketId);
    }

    public Page<Ticket> getTicketsByManagerFilter(Long managerId, ApplicationPageRequest pageable, TicketStatus status, String query, List<Long> categoryIdList) {
        return ticketPersistenceAdapter.findAllByManagerFilter(managerId, pageable, status, query, categoryIdList);
    }

    public Page<Ticket> getDepartmentTickets(String query, TicketStatus status, LocalDate startDate, LocalDate endDate, ApplicationPageRequest pageable) {
        return ticketPersistenceAdapter.findAll(query, status, startDate, endDate, pageable);
    }

    public List<DepartmentTicketExcelPreResponse> getDepartmentAllTicketsNoPaging(String query, TicketStatus status, LocalDate startDate, LocalDate endDate) {
        return ticketPersistenceAdapter.findAllTicketsNoPaging(query, status, startDate, endDate);
    }

    public Long countTicketsCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return ticketPersistenceAdapter.countByCreateAtBetween(startDate, endDate);
    }

    public Long countPinTickets(Long managerId) {
        return ticketPersistenceAdapter.countByManagerAndIsPinned(managerId);
    }

    public List<Ticket> getPinTickets(Long managerId) {
        return ticketPersistenceAdapter.findAllByManagerAndIsPinned(managerId);
    }

    public List<Ticket> getRequestTickets() {
        return ticketPersistenceAdapter.findRequests();
    }

    public List<Ticket> getMyRecentTickets(Long userId) {
        return ticketPersistenceAdapter.findRecentsByUserId(userId);
    }

    public List<Ticket> getManagersInProgressTickets(List<Long> managerIds) {
        return ticketPersistenceAdapter.findManagersInProgressTickets(managerIds);
    }

    public boolean isPinTicket(Long ticketId) {
        return ticketPersistenceAdapter.isPinTicket(ticketId);
    }

    public Page<Ticket> getTicketsByUserIdAndStatus(Long userId, TicketStatus status, ApplicationPageRequest pageRequest) {
        return ticketPersistenceAdapter.findAllByUserIdAndStatus(userId, status, pageRequest);
    }

    public String findLastSequence(String today, Category childCategory) {
        return ticketPersistenceAdapter.findLastSequence(today, childCategory);
    }
}

