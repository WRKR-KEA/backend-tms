package com.wrkr.tickety.domains.ticket.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "도움말 생성 요청 DTO", name = "GuideCreateRequest")
public record GuideCreateRequest(

        @Schema(description = "생성하고자 하는 도움말 내용", example = "vm 생성 도움말")
        String content,

        @Schema(description = "카테고리 id", example = "1")
        Long categoryId
) {
}
