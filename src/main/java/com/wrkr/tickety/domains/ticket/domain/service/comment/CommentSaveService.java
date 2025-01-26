package com.wrkr.tickety.domains.ticket.domain.service.comment;

import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.persistence.adapter.CommentPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentSaveService {

    private final CommentPersistenceAdapter commentPersistenceAdapter;

    public Comment saveComment(Comment comment) {
        return commentPersistenceAdapter.save(comment);
    }
}
