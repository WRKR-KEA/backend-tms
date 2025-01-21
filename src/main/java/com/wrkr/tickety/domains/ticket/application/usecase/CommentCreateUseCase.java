package com.wrkr.tickety.domains.ticket.application.usecase;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.CommentRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.CommentIdResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.CommentSaveService;
import com.wrkr.tickety.domains.ticket.domain.service.TicketGetService;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class CommentCreateUseCase {

	private final TicketGetService ticketGetService;
	private final CommentSaveService commentSaveService;

	public CommentIdResponse createComment(Member member, Long ticketId, CommentRequest request) {

		Ticket ticket = ticketGetService.getTicketById(ticketId);

		if (!ticket.isRelatedWith(member) || !ticket.isCommentable()) {
			throw ApplicationException.from(TicketErrorCode.TICKET_NOT_FOUND);
		}

		Long commentId = commentSaveService.saveComment(ticket, member, request.content());

		return new CommentIdResponse(PkCrypto.encrypt(commentId));
	}
}
