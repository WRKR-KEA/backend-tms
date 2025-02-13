package com.wrkr.tickety.domains.ticket.application.dto.response.ticket;

import com.querydsl.core.annotations.QueryProjection;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
@Schema(description = "부서 티켓 목록 조회 및 검색 엑셀 다운로드 프로젝션 전용 DTO")
public record DepartmentTicketExcelPreResponse(

    @Schema(description = "티켓 일련번호", example = "VM-12345678")
    String ticketSerialNumber,

    @Schema(description = "티켓 상태", example = "REQUESTED")
    TicketStatus status,

    @Schema(description = "티켓 제목", example = "VM 생성 요청")
    String title,

    @Schema(description = "티켓 1차 카테고리", example = "VM")
    String firstCategory,

    @Schema(description = "티켓 2차 카테고리", example = "생성")
    String secondCategory,

    @Schema(description = "요청자 아이디(닉네임)", example = "request.er")
    String userNickname,

    @Schema(description = "담당자 아이디(닉네임)", example = "manage.r")
    String managerNickname,

    @Schema(description = "요청일", example = "2021-01-01 00:00")
    LocalDateTime requestedDate,

    @Schema(description = "최근 업데이트일", example = "2021-01-01 00:00")
    LocalDateTime updatedDate

) {

    @QueryProjection
    public DepartmentTicketExcelPreResponse(String ticketSerialNumber, TicketStatus status, String title, String firstCategory, String secondCategory,
        String userNickname, String managerNickname,
        LocalDateTime requestedDate, LocalDateTime updatedDate
    ) {
        this.ticketSerialNumber = ticketSerialNumber;
        this.status = status;
        this.title = title;
        this.firstCategory = firstCategory;
        this.secondCategory = secondCategory;
        this.userNickname = userNickname;
        this.managerNickname = managerNickname;
        this.requestedDate = requestedDate;
        this.updatedDate = updatedDate;
    }

}
