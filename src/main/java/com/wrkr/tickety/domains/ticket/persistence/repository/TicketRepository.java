package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
