package com.wrkr.tickety.domains.ticket.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "티켓 PK 정보 DTO")
@Builder
public record TicketPkResponse(

    @Schema(description = "Ticket PK", example = "Tqs3C822lkMNdWlmE-szUw")
    String ticketId
) {

}
