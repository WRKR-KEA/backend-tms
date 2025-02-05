package com.wrkr.tickety.domains.ticket.domain.service.ticket;

import com.wrkr.tickety.domains.ticket.domain.constant.SortType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketPersistenceAdapter;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketGetService {

    private final TicketPersistenceAdapter ticketPersistenceAdapter;

    public Page<Ticket> getTicketsByUserId(Long userId, Pageable pageable) {
        return ticketPersistenceAdapter.findAllByUserId(userId, pageable);
    }

    public Ticket getTicketByTicketId(Long ticketId) {
        return ticketPersistenceAdapter.findById(ticketId);
    }

    public Page<Ticket> getTicketsByManagerFilter(Long managerId, Pageable pageable, TicketStatus status, String query, SortType sortType) {
        return ticketPersistenceAdapter.findAllByManagerFilter(managerId, pageable, status, query, sortType);
    }

    public Page<Ticket> getDepartmentTickets(String query, TicketStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return ticketPersistenceAdapter.findAll(query, status, startDate, endDate, pageable);
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
}
