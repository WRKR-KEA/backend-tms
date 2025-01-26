package com.wrkr.tickety.domains.ticket.persistence.adapter;

import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.persistence.entity.CommentEntity;
import com.wrkr.tickety.domains.ticket.persistence.mapper.CommentPersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.repository.CommentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentPersistenceAdapter {

    private final CommentRepository commentRepository;
    private final CommentPersistenceMapper commentPersistenceMapper;

    public Comment save(Comment comment) {
        CommentEntity commentEntity = commentPersistenceMapper.toEntity(comment);
        CommentEntity savedEntity = commentRepository.save(commentEntity);
        return this.commentPersistenceMapper.toDomain(savedEntity);
    }

    public List<Comment> findByTicket(final Ticket ticket) {
        final List<CommentEntity> commentEntities = commentRepository.findByTicket(ticket);
        return commentEntities.stream()
            .map(this.commentPersistenceMapper::toDomain)
            .toList();
    }

}
