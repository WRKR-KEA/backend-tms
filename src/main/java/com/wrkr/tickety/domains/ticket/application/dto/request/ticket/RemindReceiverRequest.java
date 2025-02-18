package com.wrkr.tickety.domains.ticket.application.dto.request.ticket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "티켓 리마인드 수신자 DTO")
public record RemindReceiverRequest(

    @Schema(description = "리마인드 요청할 회원 ID", example = "Bqs3C822lkMNdWlmE-szUw")
    @NotNull
    String memberId
) {

}

