package com.wrkr.tickety.domains.ticket.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
@Schema(description = "티켓 PK 정보 DTO")
public record CategoryPkResponse(

    @Schema(description = "카테고리 PK 리스트")
    List<CategoryPK> categories
) {

    @Builder
    public record CategoryPK(
        @Schema(description = "카테고리 PK", example = "Tqs3C822lkMNdWlmE-szUw")
        String categoryId
    ) {

    }
}
