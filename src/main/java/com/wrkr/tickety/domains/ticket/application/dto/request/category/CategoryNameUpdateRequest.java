package com.wrkr.tickety.domains.ticket.application.dto.request.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "카테고리 이름 변경 DTO", name = "CategoryNameUpdateRequest")
public record CategoryNameUpdateRequest(

    @NotBlank(message = "변경할 카테고리 이름은 필수 입력 값입니다.")
    @Schema(description = "변경할 카테고리 이름", example = "VM2")
    String name
) {

}
