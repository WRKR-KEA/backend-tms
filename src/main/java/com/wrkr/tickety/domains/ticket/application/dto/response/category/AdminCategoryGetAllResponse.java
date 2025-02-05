package com.wrkr.tickety.domains.ticket.application.dto.response.category;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
@Schema(description = "카테고리 전체 조회 응답 DTO", name = "AdminCategoryGetAllResponse")
public record AdminCategoryGetAllResponse(

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

        @Schema(description = "해당 카테고리 가이드 존재 여부", example = "true")
        Boolean isExistsGuide,

        @Schema(description = "해당 카테고리 템플릿 존재 여부", example = "false")
        Boolean isExistsTemplate,

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