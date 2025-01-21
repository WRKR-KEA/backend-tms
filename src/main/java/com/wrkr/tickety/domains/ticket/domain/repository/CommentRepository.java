package com.wrkr.tickety.domains.ticket.domain.repository;

import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
