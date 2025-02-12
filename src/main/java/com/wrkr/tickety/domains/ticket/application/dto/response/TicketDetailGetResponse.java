package com.wrkr.tickety.domains.ticket.application.dto.response;

import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "티켓 상세 조회 응답 DTO")
public record TicketDetailGetResponse(
    @Schema(description = "티켓 id", example = "Bqs3C822lkMNdWlmE-szUw")
    String id,

    @Schema(description = "티켓 식별번호", example = "VM001")
    String ticketSerialNumber,

    @Schema(description = "티켓 제목", example = "티켓 제목")
    String title,

    @Schema(description = "티켓 내용", example = "티켓 내용")
    String content,

    @Schema(description = "업무", example = "VM 추가")
    String category,

    @Schema(description = "티켓 상태", example = "REQUEST")
    TicketStatus status,

    @Schema(description = "요청자", example = "라이언")
    String userNickname,

    @Schema(description = "처리자", example = "춘식이")
    String managerNickname,

    @Schema(description = "생성 일시", example = "2021-01-01T00:00:00")
    String createdAt,

    @Schema(description = "수정 일시", example = "2021-01-01T00:00:00")
    String updatedAt,

    @Schema(description = "시작 일시", example = "2021-01-01T00:00:00")
    String startedAt,

    @Schema(description = "종료 일시", example = "null")
    String completedAt
) {

}
