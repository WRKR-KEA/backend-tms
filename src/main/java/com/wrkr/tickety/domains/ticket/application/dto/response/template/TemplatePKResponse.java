package com.wrkr.tickety.domains.ticket.application.dto.response.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;


@Schema(description = "템플릿 PK 정보 DTO")
@Builder
public record TemplatePKResponse(

        @Schema(description = "암호화 된 템플릿 기본 키", example = "Tqs3C822lkMNdWlmE-szUw")
        String templateId
) {

}
