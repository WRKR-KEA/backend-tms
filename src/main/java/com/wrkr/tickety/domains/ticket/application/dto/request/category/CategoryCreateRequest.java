package com.wrkr.tickety.domains.ticket.application.dto.request.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "카테고리 추가 항목 DTO", name = "CategoryCreateRequest")
public record CategoryCreateRequest(

    @NotBlank(message = "카테고리 이름은 필수 입력 값입니다.")
    @Schema(description = "카테고리 이름", example = "데이터베이스")
    String name,

    @NotNull(message = "카테고리 순서는 필수 입력 값입니다.")
    @Schema(description = "카테고리 순서", example = "1")
    Integer seq,

    @Size(min = 2, max = 2, message = "카테고리 약어는 2자여야 합니다.")
    @Pattern(regexp = "^[A-Z]+$", message = "카테고리 약어는 대문자만 입력 가능합니다.")
    @NotBlank(message = "카테고리 약어는 필수 입력 값입니다.")
    @Schema(description = "카테고리 약어", example = "DB")
    String abbreviation
) {

}
