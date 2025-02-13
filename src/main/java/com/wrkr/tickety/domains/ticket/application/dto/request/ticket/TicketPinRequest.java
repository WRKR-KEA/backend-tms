package com.wrkr.tickety.domains.ticket.application.dto.request.ticket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "티켓 고정 및 고정 취소 요청 DTO")
public record TicketPinRequest(

    @Schema(description = "고정 및 고정 취소할 티켓 ID", example = "Bqs3C822lkMNdWlmE-szUw")
    @NotNull
    String ticketId

) {

}
