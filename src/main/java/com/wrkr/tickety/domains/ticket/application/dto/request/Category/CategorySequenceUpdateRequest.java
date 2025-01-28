package com.wrkr.tickety.domains.ticket.application.dto.request.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "카테고리 순서 변경 DTO", name = "CategoryOrderUpdateRequest")
public record CategorySequenceUpdateRequest(

        @Schema(description = "카테고리 ID", example = "Gbdsnz3dU0kwFxKpavlkog==")
        String categoryId,

        @Schema(description = "카테고리 순서", example = "1")
        Integer seq
) {
}
