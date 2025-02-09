package com.wrkr.tickety.domains.ticket.application.dto.response.category;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
@Schema(description = "카테고리 전체 조회 응답 DTO", name = "UserCategoryGetAllResponse")
public record UserCategoryGetAllResponse(

    @Schema(description = "카테고리 목록")
    List<categories> categories
) {

    @Builder
    public record categories(

        @Schema(description = "카테고리 ID", example = "Bqs3C822lkMNdWlmE-szUw")
        String categoryId,

        @Schema(description = "카테고리 이름", example = "vm")
        String name,

        @Schema(description = "카테고리 순서", example = "1")
        Integer seq,

        @Schema(description = "하위 카테고리 목록")
        List<childCategories> childCategories
    ) {

        @Builder
        public record childCategories(

            @Schema(description = "카테고리 ID", example = "Bqs3C822lkMNdWlmE-szUw")
            String categoryId,

            @Schema(description = "카테고리 이름", example = "vm")
            String name,

            @Schema(description = "카테고리 순서", example = "1")
            Integer seq
        ) {

        }

    }
}