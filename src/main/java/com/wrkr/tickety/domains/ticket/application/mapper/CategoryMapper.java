package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.application.dto.request.Category.CategoryCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.category.CategoryGetAllResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.global.utils.PkCrypto;

import java.util.List;

public class CategoryMapper {

    private CategoryMapper() {
        throw new IllegalArgumentException();
    }

    public static List<CategoryGetAllResponse> mapToCategoryGetAllResponseDTO(List<Category> categoryList) {
        return categoryList.stream()
                .map(category -> CategoryGetAllResponse.builder()
                        .categoryId(PkCrypto.encrypt(category.getCategoryId()))
                        .name(category.getName())
                        .seq(category.getSeq())
                        .isExistsGuide(null)
                        .isExistsTemplate(null)
                        .build()
                ).toList();
    }

    public static Category mapToCategoryDomain(CategoryCreateRequest request) {
        return Category.builder()
                .name(request.name())
                .seq(request.seq())
                .build();
    }

    public static PkResponse mapToPkResponse(Category category) {
        return PkResponse.builder()
                .id(PkCrypto.encrypt(category.getCategoryId()))
                .build();
    }
}
