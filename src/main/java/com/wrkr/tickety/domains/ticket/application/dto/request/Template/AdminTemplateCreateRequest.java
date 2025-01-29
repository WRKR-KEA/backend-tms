package com.wrkr.tickety.domains.ticket.application.dto.request.Template;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리에 해당하는 템플릿 작성 DTO", name = "AdminTemplateCreateRequest")
public record AdminTemplateCreateRequest(

        @Schema(description = "카테고리 ID", example = "Gbdsnz3dU0kwFxKpavlkog==")
        String categoryId,

        @Schema(description = "템플릿 내용", example = "VM을 생성하려면 다음과 같이 작성해주세요...")
        String content
) {
}
