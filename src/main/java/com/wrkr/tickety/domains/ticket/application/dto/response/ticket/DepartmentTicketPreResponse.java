package com.wrkr.tickety.domains.ticket.application.dto.response.ticket;

import com.querydsl.core.annotations.QueryProjection;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
@Schema(description = "부서 전체 티켓 조회 및 검색 응답(엔티티 타입에 맞춘 DTO)", name = "부서 전체 티켓 조회 및 검색 응답")
public record DepartmentTicketPreResponse(
    @Schema(description = "티켓 ID", example = "1")
    Long ticketId,

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

    @Schema(description = "요청일", example = "2021-01-01 10:00:00")
    LocalDateTime requestedDate,

    @Schema(description = "최근 업데이트일", example = "2021-01-01 10:00:00")
    LocalDateTime updatedDate
) {

    @QueryProjection
    public DepartmentTicketPreResponse(Long ticketId, String ticketSerialNumber, TicketStatus status, String title, String userNickname, String managerNickname,
        LocalDateTime requestedDate, LocalDateTime updatedDate) {
        this.ticketId = ticketId;
        this.ticketSerialNumber = ticketSerialNumber;
        this.status = status;
        this.title = title;
        this.userNickname = userNickname;
        this.managerNickname = managerNickname;
        this.requestedDate = requestedDate;
        this.updatedDate = updatedDate;
    }
}
