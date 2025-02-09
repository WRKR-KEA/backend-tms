package com.wrkr.tickety.domains.ticket.domain.service.ticket;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketUpdateService {

    private final TicketPersistenceAdapter ticketPersistenceAdapter;

    public Ticket updateStatus(Ticket ticket, TicketStatus ticketStatus) {
        ticket.updateStatus(ticketStatus);
        return ticketPersistenceAdapter.save(ticket);
    }

    public Ticket approveTicket(Ticket ticket, Member member) {
        ticket.approveTicket(member);
        return ticketPersistenceAdapter.save(ticket);
    }

    public Ticket updateManager(Ticket ticket, Member manager) {
        ticket.updateManager(manager);
        return ticketPersistenceAdapter.save(ticket);
    }

    public Ticket pinTicket(Ticket ticket) {
        ticket.pinTicket(ticket);
        return ticketPersistenceAdapter.save(ticket);
    }
}
