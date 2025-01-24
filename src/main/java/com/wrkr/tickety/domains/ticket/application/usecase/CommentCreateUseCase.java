package com.wrkr.tickety.domains.ticket.application.usecase;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.CommentRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.CommentIdResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.CommentSaveService;
import com.wrkr.tickety.domains.ticket.domain.service.TicketGetService;
import com.wrkr.tickety.domains.ticket.exception.CommentErrorCode;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
@RequiredArgsConstructor
public class CommentCreateUseCase {

    private final TicketGetService ticketGetService;
    private final CommentSaveService commentSaveService;

    public CommentIdResponse createComment(Member member, Long ticketId, CommentRequest request) {

        Ticket ticket = ticketGetService.byId(ticketId);

        if (!ticket.isRelatedWith(member)) {
            throw ApplicationException.from(TicketErrorCode.TICKET_NOT_FOUND);
        }
        if (!ticket.isCommentable()) {
            throw ApplicationException.from(CommentErrorCode.TICKET_STATUS_INVALID_FOR_COMMENT);
        }

        Long commentId = commentSaveService.saveComment(ticket, member, request.content());

        return CommentIdResponse.builder()
            .commentId(PkCrypto.encrypt(commentId))
            .build();
    }
}
