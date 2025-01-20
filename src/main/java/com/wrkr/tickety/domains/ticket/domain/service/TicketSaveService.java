package com.wrkr.tickety.domains.ticket.domain.service;

import com.wrkr.tickety.domains.ticket.application.dto.request.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.persistence.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketSaveService {

    private final TicketRepository ticketRepository;

    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }
}
