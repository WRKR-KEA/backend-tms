package com.wrkr.tickety.domains.ticket.application.dto.response;

import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "티켓 전체 조회 응답 DTO")
public record TicketAllGetResponse(
    @Schema(description = "티켓 id", example = "ouqJF8uKst63ZPA2T70jda")
    String id,

    @Schema(description = "담당자 이름", example = "춘식이")
    String managerName,

    @Schema(description = "티켓 일련번호", example = "VM-12345678")
    String serialNumber,

    @Schema(description = "티켓 제목", example = "티켓 제목")
    String title,

    @Schema(description = "티켓 상태", example = "REQUEST")
    TicketStatus status,

    @Schema(description = "티켓 1차 카테고리", example = "VM")
    String firstCategory,

    @Schema(description = "티켓 2차 카테고리", example = "생성")
    String secondCategory,

    @Schema(description = "요청일", example = "2021.08.01 00:00")
    String createdAt,

    @Schema(description = "시작일", example = "2021.08.01 00:00")
    String startedAt,

    @Schema(description = "최근 업데이트일", example = "2021.08.01 00:00")
    String updatedAt
) {

}
