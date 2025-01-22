package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.application.dto.response.category.CategoryGetAllResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Category;

import java.util.List;

public class CategoryMapper {

    private CategoryMapper() {
        throw new IllegalArgumentException();
    }


    public static List<CategoryGetAllResponse> mapToCategoryGetAllResponseDTO(List<Category> categoryList) {

        return categoryList.stream()
                .map(category -> CategoryGetAllResponse.builder()
                        .categoryId(category.getCategoryId())
                        .name(category.getName())
                        .seq(category.getSeq())
                        .isExistsGuide(null)
                        .isExistsTemplate(null)
                        .build()
                ).toList();
    }
}
