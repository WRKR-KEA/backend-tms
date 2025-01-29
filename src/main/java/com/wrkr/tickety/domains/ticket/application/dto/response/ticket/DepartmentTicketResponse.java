package com.wrkr.tickety.domains.ticket.application.dto.response.ticket;

import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "부서 전체 티켓 조회 및 검색 응답 DTO", name = "부서 전체 티켓 조회 및 검색 응답")
public record DepartmentTicketResponse(

    @Schema(description = "티켓 ID", example = "W1NMMfAHGTnNGLdRL3lvcw")
    String ticketId,

    @Schema(description = "티켓 일련번호", example = "VM-12345678")
    String ticketSerialNumber,

    @Schema(description = "티켓 상태", example = "REQUESTED")
    TicketStatus status,

    @Schema(description = "티켓 제목", example = "VM 생성 요청")
    String title,

    @Schema(description = "사용자 닉네임", example = "request.er")
    String userNickname,

    @Schema(description = "담당자 닉네임", example = "manage.r")
    String managerNickname,

    @Schema(description = "요청일", example = "2021-01-01")
    String requestedDate,

    @Schema(description = "최근 업데이트일", example = "2021-01-01")
    String updatedDate
) {

}
