package com.wrkr.tickety.domains.ticket.domain.service;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentSaveService {

	private final CommentRepository commentRepository;

	public Long saveComment(Ticket ticket, Member member, String content) {
		Comment newComment = Comment.builder()
				.ticket(ticket)
				.member(member)
				.content(content)
				.build();
		return commentRepository.save(newComment).getCommentId();
	}

	public Long saveSystemComment(Ticket ticket, String content) {
		Comment newComment = Comment.builder()
				.ticket(ticket)
				.content(content)
				.build();
		return commentRepository.save(newComment).getCommentId();
	}
}
