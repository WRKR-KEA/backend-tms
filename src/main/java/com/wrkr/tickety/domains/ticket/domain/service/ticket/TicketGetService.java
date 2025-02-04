package com.wrkr.tickety.domains.ticket.domain.service.ticket;

import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketPersistenceAdapter;
import com.wrkr.tickety.global.common.dto.PageRequest;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketGetService {

    private final TicketPersistenceAdapter ticketPersistenceAdapter;

    public Page<Ticket> getTicketsByUserId(Long userId, PageRequest pageRequest) {
        return ticketPersistenceAdapter.findAllByUserId(userId, pageRequest);
    }

    public Ticket getTicketByTicketId(Long ticketId) {
        return ticketPersistenceAdapter.findById(ticketId);
    }

    public Page<Ticket> getTicketsByManagerFilter(Long managerId, PageRequest pageable, TicketStatus status, String query) {
        return ticketPersistenceAdapter.findAllByManagerFilter(managerId, pageable, status, query);
    }

    public Page<Ticket> getDepartmentTickets(String query, TicketStatus status, LocalDate startDate, LocalDate endDate, PageRequest pageable) {
        return ticketPersistenceAdapter.findAll(query, status, startDate, endDate, pageable);
    }
}
