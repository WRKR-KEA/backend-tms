package com.wrkr.tickety.domains.ticket.application.dto.response;

import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "담당자 자신이 담당한 티켓 목록 조회 응답")
public record ManagerTicketAllGetResponse(

    @Schema(description = "티켓 id", example = "ouqJF8uKst63ZPA2T70jda")
    String id,

    @Schema(description = "티켓 일련번호", example = "VM-12345678")
    String serialNumber,

    @Schema(description = "티켓 제목", example = "티켓 제목")
    String title,

    @Schema(description = "티켓 상태", example = "IN_PROGRESS")
    TicketStatus status,

    @Schema(description = "요청자 닉네임", example = "홍길동")
    String requesterNickname,

    @Schema(description = "요청일", example = "2021.08.01")
    String createdAt,

    @Schema(description = "최근 업데이트일", example = "2021.08.01")
    String updatedAt,

    @Schema(description = "고정 여부", example = "true")
    boolean isPinned
) {

}
