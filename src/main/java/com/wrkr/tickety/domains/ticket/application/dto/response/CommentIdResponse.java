package com.wrkr.tickety.domains.ticket.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CommentIdResponse(

		@Schema(description = "코멘트 ID", example = "ouqJF8uKst63ZPA2T70jda")
        String commentId
) {
}
