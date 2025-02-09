package com.wrkr.tickety.domains.ticket.application.dto.request.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "카테고리 순서 변경 DTO", name = "CategorySequenceUpdateRequest")
public record CategorySequenceUpdateRequest(

    @NotBlank(message = "카테고리 ID는 필수 입력 값입니다.")
    @Schema(description = "카테고리 ID", example = "Gbdsnz3dU0kwFxKpavlkog==")
    String categoryId,

    @NotNull(message = "카테고리 순서는 필수 입력 값입니다.")
    @Schema(description = "카테고리 순서", example = "1")
    Integer seq
) {

}
