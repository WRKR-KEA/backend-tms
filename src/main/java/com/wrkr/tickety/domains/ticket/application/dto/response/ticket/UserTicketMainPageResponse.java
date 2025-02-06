package com.wrkr.tickety.domains.ticket.application.dto.response.ticket;

import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
@Schema(description = "사용자 메인페이지 DTO ", name = "사용자 메인페이지 티켓 구성 목록")
public record UserTicketMainPageResponse(

    @Schema(description = "최근 티켓 목록")
    List<recentTickets> recentTickets
) {

    @Builder
    public record recentTickets(
        @Schema(description = "티켓 ID", example = "W1NMMfAHGTnNGLdRL3lvcw")
        String ticketId,

        @Schema(description = "티켓 일련번호", example = "VM-12345678")
        String ticketSerialNumber,

        @Schema(description = "티켓 상태", example = "REQUESTED")
        TicketStatus status,

        @Schema(description = "티켓 제목", example = "VM 생성 요청")
        String title,

        @Schema(description = "담당자 닉네임", example = "manage.r")
        String managerNickname,

        @Schema(description = "사용자 닉네임", example = "request.er")
        String userNickname,

        @Schema(description = "요청일", example = "2021-01-01")
        String requestedDate,

        @Schema(description = "최근 업데이트일", example = "2021-01-01")
        String updatedDate,

        @Schema(description = "티켓 시간 정보")
        ticketTimeInfo ticketTimeInfo
    ) {

        @Builder
        public record ticketTimeInfo(

            @Schema(description = "생성 일시", example = "2021-01-01T00:00:00")
            LocalDateTime createdAt,

            @Schema(description = "수정 일시", example = "2021-01-01T00:00:00")
            LocalDateTime updatedAt,

            @Schema(description = "시작 일시", example = "2021-01-01T00:00:00")
            LocalDateTime startedAt,

            @Schema(description = "종료 일시", example = "2021-01-01T00:00:00")
            LocalDateTime endedAt
        ) {

        }

    }

}
