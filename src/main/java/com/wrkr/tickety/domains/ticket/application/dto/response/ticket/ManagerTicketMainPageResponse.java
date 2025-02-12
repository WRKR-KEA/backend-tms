package com.wrkr.tickety.domains.ticket.application.dto.response.ticket;

import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
@Schema(description = "담당자 메인페이지 DTO", name = "담당자 메인페이지 구성 티켓 목록")
public record ManagerTicketMainPageResponse(

    @Schema(description = "고정된 티켓 목록")
    List<PinTickets> pinTickets,

    @Schema(description = "요청된 티켓 목록")
    List<requestTickets> requestTickets

) {

    @Builder
    public record PinTickets(
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

        @Schema(description = "요청일", example = "2021-01-01 00:00")
        String requestedDate,

        @Schema(description = "최근 업데이트일", example = "2021-01-01 00:00")
        String updatedDate
    ) {

    }

    @Builder
    public record requestTickets(
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

        @Schema(description = "요청일", example = "2021-01-01 00:00")
        String requestedDate,

        @Schema(description = "최근 업데이트일", example = "2021-01-01 00:00")
        String updatedDate
    ) {

    }

}
