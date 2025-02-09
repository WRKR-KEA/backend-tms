package com.wrkr.tickety.domains.ticket.application.dto.request.template;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "카테고리에 해당하는 템플릿 수정 DTO", name = "AdminTemplateUpdateRequest")
public record AdminTemplateUpdateRequest(

    @NotBlank(message = "템플릿 내용은 필수 입력 값입니다.")
    @Schema(description = "템플릿 내용", example = "VM을 생성하려면 다음과 같이 작성해주세요...")
    String content
) {

}
