package com.wrkr.tickety.domains.ticket.application.dto.request.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "카테고리 추가 항목 DTO", name = "CategoryCreateRequest")
public record CategoryCreateRequest(

    @NotBlank(message = "카테고리 이름은 필수 입력 값입니다.")
    @Schema(description = "카테고리 이름", example = "vm")
    String name,

    @NotNull(message = "카테고리 순서는 필수 입력 값입니다.")
    @Schema(description = "카테고리 순서", example = "1")
    Integer seq
) {

}
