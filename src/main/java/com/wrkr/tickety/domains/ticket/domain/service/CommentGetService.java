package com.wrkr.tickety.domains.ticket.domain.service;

import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentGetService {

	private final CommentRepository commentRepository;

	public List<Comment> byTicket(Ticket ticket) {
		return commentRepository.findByTicket(ticket);
	}
}
