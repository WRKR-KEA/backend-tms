package com.wrkr.tickety.domains.ticket.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "해당 API 주체를 담은 dto", name = "PkResponse")
public record PkResponse(

        @Schema(description = "해당 API 주체의 ID", example = "Bqs3C822lkMNdWlmE-szUw")
        String id
) {
}
