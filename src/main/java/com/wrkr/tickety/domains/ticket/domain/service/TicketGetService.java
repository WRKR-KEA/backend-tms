package com.wrkr.tickety.domains.ticket.domain.service;

import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.repository.TicketRepository;
import com.wrkr.tickety.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketGetService {

	private final TicketRepository ticketRepository;

	public Ticket byId(Long ticketId) {

		return ticketRepository.findById(ticketId)
				.orElseThrow(() -> ApplicationException.from(TicketErrorCode.TICKET_NOT_FOUND));
	}
}
