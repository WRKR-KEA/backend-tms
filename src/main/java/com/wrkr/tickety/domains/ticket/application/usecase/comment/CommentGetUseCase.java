package com.wrkr.tickety.domains.ticket.application.usecase.comment;

import com.wrkr.tickety.domains.attachment.domain.service.CommentAttachmentGetService;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.response.CommentResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.CommentMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.comment.CommentGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentGetUseCase {

    private final TicketGetService ticketGetService;
    private final CommentGetService commentGetService;
    private final CommentAttachmentGetService commentAttachmentGetService;

    public CommentResponse getComment(Member member, Long ticketId) {

        Ticket ticket = ticketGetService.getTicketByTicketId(ticketId);

        if (!ticket.isAccessibleBy(member)) {
            throw ApplicationException.from(TicketErrorCode.UNAUTHORIZED_ACCESS);
        }

        List<Comment> comments = commentGetService.getCommentsByTicket(ticket);

        return CommentMapper.toCommentResponse(ticketId, comments, commentAttachmentGetService);
    }
}
