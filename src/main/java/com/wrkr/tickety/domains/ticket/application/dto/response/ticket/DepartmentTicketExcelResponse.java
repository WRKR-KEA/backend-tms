package com.wrkr.tickety.domains.ticket.application.dto.response.ticket;

import com.wrkr.tickety.global.utils.excel.ExcelColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "부서 티켓 목록 조회 및 검색 엑셀 다운로드 DTO")
public record DepartmentTicketExcelResponse(

    @Schema(description = "티켓 일련번호", example = "VM-12345678")
    @ExcelColumn(headerName = "티켓 일련번호")
    String ticketSerialNumber,

    @Schema(description = "티켓 상태", example = "REQUESTED")
    @ExcelColumn(headerName = "상태")
    String status,

    @Schema(description = "티켓 제목", example = "VM 생성 요청")
    @ExcelColumn(headerName = "제목")
    String title,

    @Schema(description = "티켓 1차 카테고리", example = "VM")
    @ExcelColumn(headerName = "1차 카테고리")
    String firstCategory,

    @Schema(description = "티켓 2차 카테고리", example = "생성")
    @ExcelColumn(headerName = "2차 카테고리")
    String secondCategory,

    @Schema(description = "사용자 닉네임", example = "request.er")
    @ExcelColumn(headerName = "요청자")
    String userNickname,

    @Schema(description = "담당자 닉네임", example = "manage.r")
    @ExcelColumn(headerName = "담당자")
    String managerNickname,

    @Schema(description = "요청일", example = "2021-01-01 00:00")
    @ExcelColumn(headerName = "요청일")
    String requestedDate,

    @Schema(description = "최근 업데이트일", example = "2021-01-01 00:00")
    @ExcelColumn(headerName = "최근 업데이트일")
    String updatedDate

) {

}
