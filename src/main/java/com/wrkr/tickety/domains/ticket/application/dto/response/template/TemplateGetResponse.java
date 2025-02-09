package com.wrkr.tickety.domains.ticket.application.dto.response.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "템플릿 조회 응답 DTO", name = "TemplateGetResponse")
public record TemplateGetResponse(

    @Schema(description = "템플릿 ID", example = "Gbdsnz3dU0kwFxKpavlkog==")
    String templateId,
    @Schema(description = "카테고리 ID", example = "Gbdsnz3dU0kwFxKpavlkog==")
    String categoryId,
    @Schema(description = "템플릿 내용", example = "템플릿 내용")
    String content
) {

}
