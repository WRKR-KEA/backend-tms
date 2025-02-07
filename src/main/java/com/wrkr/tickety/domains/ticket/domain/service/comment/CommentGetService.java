package com.wrkr.tickety.domains.ticket.domain.service.comment;

import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.exception.CommentErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.adapter.CommentPersistenceAdapter;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentGetService {

    private final CommentPersistenceAdapter commentPersistenceAdapter;

    public List<Comment> getCommentsByTicket(Ticket ticket) {
        return commentPersistenceAdapter.findByTicket(ticket);
    }

    public Comment getCommentById(Long commentId) {
        return commentPersistenceAdapter.findById(commentId)
            .orElseThrow(() -> ApplicationException.from(CommentErrorCode.COMMENT_NOT_FOUND));
    }
}
