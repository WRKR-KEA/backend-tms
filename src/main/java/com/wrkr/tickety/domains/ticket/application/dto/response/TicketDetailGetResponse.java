package com.wrkr.tickety.domains.ticket.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record TicketDetailGetResponse(
        @Schema(description = "티켓 id", example = "Bqs3C822lkMNdWlmE-szUw")
        String id,

        @Schema(description = "티켓 제목", example = "티켓 제목")
        String title,

        @Schema(description = "티켓 내용", example = "티켓 내용")
        String content,

        @Schema(description = "티켓 상태", example = "OPEN")
        String status,

        @Schema(description = "요청자", example = "라이언")
        String userName,

        @Schema(description = "처리자", example = "춘식이")
        String managerName,

        @Schema(description = "생성 일시", example = "2021-01-01T00:00:00")
        LocalDateTime createdAt,

        @Schema(description = "수정 일시", example = "2021-01-01T00:00:00")
        LocalDateTime updatedAt,

        @Schema(description = "시작 일시", example = "2021-01-01T00:00:00")
        LocalDateTime startedAt
) {
}
