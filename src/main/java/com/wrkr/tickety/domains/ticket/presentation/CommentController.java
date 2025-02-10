package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.CommentRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.CommentResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.comment.CommentCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.comment.CommentGetUseCase;
import com.wrkr.tickety.domains.ticket.exception.CommentErrorCode;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.utils.PkCrypto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comment Controller")
public class CommentController {

    private final CommentCreateUseCase commentCreateUseCase;
    private final CommentGetUseCase commentGetUseCase;

    @PostMapping(value = "/api/user/tickets/{ticketId}/comments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CustomErrorCodes(ticketErrorCodes = {TicketErrorCode.TICKET_NOT_FOUND, TicketErrorCode.UNAUTHORIZED_ACCESS},
        commentErrorCodes = {CommentErrorCode.COMMENT_CONFLICT})
    @Operation(summary = "코멘트 작성", description = "티켓에 대해 코멘트를 작성합니다.")
    @Parameter(name = "attachments", description = "첨부파일")
    @Parameter(
        name = "CommentRequest",
        description = "도움말 생성 요청 데이터",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = CommentRequest.class)
        )
    )
    public ApplicationResponse<PkResponse> createComment(
        @AuthenticationPrincipal Member member,
        @RequestPart("CommentRequest") CommentRequest commentRequest,
        @PathVariable String ticketId,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        return ApplicationResponse.onSuccess(commentCreateUseCase.createComment(member, PkCrypto.decrypt(ticketId), commentRequest, attachments));
    }

    @GetMapping("/api/user/tickets/{ticketId}/comments")
    @CustomErrorCodes(ticketErrorCodes = {TicketErrorCode.TICKET_NOT_FOUND, TicketErrorCode.UNAUTHORIZED_ACCESS})
    @Operation(summary = "코멘트 내역 조회", description = "티켓에 대해 코멘트 내역을 조회합니다.")
    public ApplicationResponse<CommentResponse> getComment(@AuthenticationPrincipal Member member, @PathVariable String ticketId) {
        return ApplicationResponse.onSuccess(commentGetUseCase.getComment(member, PkCrypto.decrypt(ticketId)));
    }
}
