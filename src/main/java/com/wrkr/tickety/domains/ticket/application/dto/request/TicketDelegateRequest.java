package com.wrkr.tickety.domains.ticket.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "티켓 담당자 변경 요청 DTO")
public record TicketDelegateRequest(

    @Schema(description = "위임할 담당자 ID", example = "Bqs3C822lkMNdWlmE-szUw")
    @NotNull
    String delegateManagerId
) {

}
