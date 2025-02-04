package com.wrkr.tickety.domains.ticket.application.usecase.comment;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.CommentRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.domain.event.CommentCreateEvent;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.comment.CommentSaveService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.exception.CommentErrorCode;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
@RequiredArgsConstructor
public class CommentCreateUseCase {

    private final TicketGetService ticketGetService;
    private final CommentSaveService commentSaveService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public PkResponse createComment(Member member, Long ticketId, CommentRequest request) {

        Ticket ticket = ticketGetService.getTicketByTicketId(ticketId);

        if (!ticket.isRelatedWith(member)) {
            throw ApplicationException.from(TicketErrorCode.UNAUTHORIZED_ACCESS);
        }
        if (!ticket.isCommentable()) {
            throw ApplicationException.from(CommentErrorCode.COMMENT_CONFLICT);
        }

        Comment comment = Comment.builder()
            .ticket(ticket)
            .member(member)
            .content(request.content())
            .build();

        Comment savedComment = commentSaveService.saveComment(comment);

        applicationEventPublisher.publishEvent(CommentCreateEvent.builder()
                                                   .comment(savedComment)
                                                   .build());

        return PkResponse.builder()
            .id(PkCrypto.encrypt(savedComment.getCommentId()))
            .build();
    }
}
