package com.wrkr.tickety.domains.ticket.application.dto.response.ticket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "매니저 티켓 고정 및 고정 취소 성공 응답 DTO", name = "매니저 티켓 고정 및 고정 취소 성공 응답")
public record ManagerPinTicketResponse(

    @Schema(description = "티켓 ID", example = "W1NMMfAHGTnNGLdRL3lvcw")
    String ticketId,

    @Schema(description = "고정 여부", example = "true")
    Boolean isPinned

) {

}
