package com.wrkr.tickety.domains.ticket.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CommentRequest(

		@Schema(description = "코멘트 내용", example = "이슈 다시 확인해주세요.")
		@NotBlank(message = "코멘트 내용은 필수 항목입니다.")
        String content
) {
}
