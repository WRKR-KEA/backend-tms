package com.wrkr.tickety.domains.ticket.domain.service.ticket;

import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketPersistenceAdapter;
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
}
