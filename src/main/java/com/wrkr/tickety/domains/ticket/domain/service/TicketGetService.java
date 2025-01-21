package com.wrkr.tickety.domains.ticket.domain.service;

import com.wrkr.tickety.domains.ticket.application.dto.response.TicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.persistence.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketGetService {

    private final TicketRepository ticketRepository;

    public Page<Ticket> getTicketsByUserId(Long userId, Pageable pageable) {
        return ticketRepository.findAllByUserId(userId, pageable);
    }

    public Optional<Ticket> getTicketByTicketId(Long ticketId) {
        return ticketRepository.findById(ticketId);
    }
}
