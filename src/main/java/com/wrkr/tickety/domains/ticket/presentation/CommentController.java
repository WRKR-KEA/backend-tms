package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.CommentRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.CommentIdResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.CommentCreateUseCase;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.utils.PkCrypto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comment Controller")
public class CommentController {

	private final CommentCreateUseCase commentCreateUseCase;

	@PostMapping("/api/user/tickets/{ticketId}/comments")
	@Operation(summary = "코멘트 작성", description = "티켓에 대해 코멘트를 작성합니다.")
	public ApplicationResponse<CommentIdResponse> createComment(@PathVariable String ticketId, @RequestBody CommentRequest request) {

		return ApplicationResponse.onSuccess(commentCreateUseCase.createComment(Member.builder().build(), PkCrypto.decrypt(ticketId), request));
	}
}
