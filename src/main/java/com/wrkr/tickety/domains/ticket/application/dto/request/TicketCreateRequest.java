package com.wrkr.tickety.domains.ticket.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TicketCreateRequest(

        @Schema(description = "티켓 제목", example = "티켓 제목")
        @NotBlank(message = "티켓 제목은 필수 항목입니다.")
        String title,

        @Schema(description = "티켓 내용", example = "티켓 내용")
        @NotBlank(message = "티켓 내용은 필수 항목입니다.")
        String content,

        @Schema(description = "2차 카테고리", example = "카테고리 ID")
        @NotNull(message = "2차 카테고리는 필수 항목입니다.")
        String categoryId

) {
}
