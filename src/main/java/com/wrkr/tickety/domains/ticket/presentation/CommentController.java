package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.CommentRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.CommentIdResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.CommentResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.CommentCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.CommentGetUseCase;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.utils.PkCrypto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comment Controller")
public class CommentController {

	private final CommentCreateUseCase commentCreateUseCase;
	private final CommentGetUseCase commentGetUseCase;

	@PostMapping("/api/user/tickets/{ticketId}/comments")
	@Operation(summary = "코멘트 작성", description = "티켓에 대해 코멘트를 작성합니다.")
	public ApplicationResponse<CommentIdResponse> createComment(@PathVariable String ticketId, @RequestBody CommentRequest request) {

		return ApplicationResponse.onSuccess(commentCreateUseCase.createComment(Member.builder().build(), PkCrypto.decrypt(ticketId), request));
	}

	@GetMapping("/api/user/tickets/{ticketId}/comments")
	@Operation(summary = "코멘트 내역 조회", description = "티켓에 대해 코멘트 내역을 조회합니다.")
	public ApplicationResponse<CommentResponse> getComment(@PathVariable String ticketId) {

		return ApplicationResponse.onSuccess(commentGetUseCase.getComment(Member.builder().build(), PkCrypto.decrypt(ticketId)));
	}
}
