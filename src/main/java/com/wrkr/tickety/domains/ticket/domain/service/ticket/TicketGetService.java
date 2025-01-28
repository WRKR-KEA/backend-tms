package com.wrkr.tickety.domains.ticket.domain.service.ticket;

import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketPersistenceAdapter;
import java.time.LocalDate;
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

    public Page<Ticket> getTicketsByManagerFilter(Long managerId, Pageable pageable, TicketStatus status, String search) {
        if (status == null) {
            return ticketPersistenceAdapter.findAllByManagerFilter(managerId, pageable, null, search);
        }
        return ticketPersistenceAdapter.findAllByManagerFilter(managerId, pageable, status, search);

    }

    public Page<Ticket> getDepartmentTickets(String query, TicketStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return ticketPersistenceAdapter.findAll(query, status, startDate, endDate, pageable);
    }
}
