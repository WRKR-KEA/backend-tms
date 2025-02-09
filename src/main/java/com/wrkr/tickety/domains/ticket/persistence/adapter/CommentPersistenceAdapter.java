package com.wrkr.tickety.domains.ticket.persistence.adapter;

import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.persistence.entity.CommentEntity;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import com.wrkr.tickety.domains.ticket.persistence.mapper.CommentPersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.mapper.TicketPersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.repository.CommentRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentPersistenceAdapter {

    private final CommentRepository commentRepository;
    private final CommentPersistenceMapper commentPersistenceMapper;
    private final TicketPersistenceMapper ticketPersistenceMapper;

    public Comment save(Comment comment) {
        CommentEntity commentEntity = commentPersistenceMapper.toEntity(comment);
        CommentEntity savedEntity = commentRepository.save(commentEntity);
        return this.commentPersistenceMapper.toDomain(savedEntity);
    }

    public List<Comment> findByTicket(final Ticket ticket) {
        TicketEntity ticketEntity = ticketPersistenceMapper.toEntity(ticket);
        final List<CommentEntity> commentEntities = commentRepository.findByTicket(ticketEntity);
        return commentEntities.stream()
            .map(this.commentPersistenceMapper::toDomain)
            .toList();
    }

    public Optional<Comment> findById(Long commentId) {
        return commentRepository.findById(commentId)
            .map(this.commentPersistenceMapper::toDomain);
    }
}
