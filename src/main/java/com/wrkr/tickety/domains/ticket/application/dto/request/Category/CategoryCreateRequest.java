package com.wrkr.tickety.domains.ticket.application.dto.request.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "카테고리 추가 항목 DTO", name = "CategoryCreateRequest")
public record CategoryCreateRequest(

        @Schema(description = "카테고리 이름", example = "vm")
        String name,

        @Schema(description = "카테고리 순서", example = "1")
        Integer seq
) {
}
