package com.wrkr.tickety.domains.ticket.application.usecase;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.response.CommentResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.CommentGetService;
import com.wrkr.tickety.domains.ticket.domain.service.TicketGetService;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentGetUseCase {

	private final TicketGetService ticketGetService;
	private final CommentGetService commentGetService;

	public CommentResponse getComment(Member member, Long ticketId) {

		Ticket ticket = ticketGetService.byId(ticketId);

		if (member.getRole().equals(Role.USER) && !ticket.isRelatedWith(member)) {
			throw ApplicationException.from(TicketErrorCode.TICKET_NOT_FOUND);
		}

		List<Comment> comments = commentGetService.byTicket(ticket);

		return CommentResponse.from(ticketId, comments);
	}
}
