package com.wrkr.tickety.domains.ticket.application.dto.request.Ticket;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "부서 전체 티켓 조회 및 검색 요청 DTO", name = "부서 전체 티켓 조회 및 검색 요청")
public record DepartmentTicketRequest(

    @Schema(description = "검색어 (제목, 담당자, 티켓 번호 대상)", example = "VM")
    String query,

    @Schema(description = "필터링 - 티켓 상태", example = "PROCESSING", examples = {"REQUESTED", "CANCELED", "ACCEPTED", "PROCESSING", "ABORTED", "COMPLETED"})
    String status,

    @Schema(description = "필터링 - 요청일 시작", example = "2025-01-27")
    String startDate,

    @Schema(description = "필터링 - 요청일 끝", example = "2025-01-27")
    String endDate
) {

}
