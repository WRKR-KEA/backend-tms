package com.wrkr.tickety.domains.ticket.domain.service;

import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketSaveService {

    private final TicketPersistenceAdapter ticketPersistenceAdapter;

    public Ticket save(Ticket ticket) {
        return ticketPersistenceAdapter.save(ticket);
    }
}
