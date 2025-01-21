package com.wrkr.tickety.domains.ticket.application.dto.response;

import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.global.utils.PkCrypto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record CommentResponse(

		@Schema(description = "티켓 ID", example = "ouqJF8uKst63ZPA2T70jda")
		String ticketId,

		@Schema(description = "코멘트 목록")
		List<CommentDTO> comments
) {
	public record CommentDTO(

			@Schema(description = "코멘트를 전송한 주체", examples = {"USER", "MANAGER", "SYSTEM"})
			String type,

			@Schema(description = "코멘트 ID", example = "ouqJF8uKst63ZPA2T70jda")
			String commentId,

			@Schema(description = "코멘트 전송일", example = "2025-01-21T14:50:30")
			LocalDateTime createdAt,

			@Schema(description = "코멘트 전송자가 회원일 경우, 회원 ID", nullable = true, example = "ouqJF8uKst63ZPA2T70jda")
			String memberId,

			@Schema(description = "코멘트 전송자가 회원일 경우, 회원 이름", nullable = true, example = "김철수")
			String name,

			@Schema(description = "코멘트 내용", example = "이슈 다시 확인해주세요.")
			String content
	) {}

	public static CommentResponse from(Long ticketId, List<Comment> comments) {

		List<CommentDTO> commentDTOList = new ArrayList<>();
		for (Comment comment : comments) {

			commentDTOList.add(new CommentDTO(
					comment.getMember() == null ? "system" : comment.getMember().getRole().toString(),
					PkCrypto.encrypt(comment.getCommentId()),
					comment.getCreatedAt(),
					PkCrypto.encrypt(comment.getMember().getMemberId()),
					comment.getMember().getName(),
					comment.getContent()
			));
		}

		return new CommentResponse(PkCrypto.encrypt(ticketId), commentDTOList);
	}
}
