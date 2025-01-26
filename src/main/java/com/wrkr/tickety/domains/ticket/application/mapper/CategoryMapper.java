package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.application.dto.request.Category.CategoryCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.Category.CategoryUpdateRequest;
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

    public static PkResponse mapToPkResponse(Category category) {
        return PkResponse.builder()
                .id(PkCrypto.encrypt(category.getCategoryId()))
                .build();
    }
    public static Category mapToCategoryDomain(CategoryCreateRequest request) {
        return Category.builder()
                .name(request.name())
                .seq(request.seq())
                .build();
    }

    public static List<Category> initChildren(Category savedCategory) {
        Category createChildCategory = Category.builder()
                .name("생성")
                .seq(1)
                .parent(savedCategory)
                .build();

        Category deleteChildCategory = Category.builder()
                .name("삭제")
                .seq(2)
                .parent(savedCategory)
                .build();

        Category updateChildCategory = Category.builder()
                .name("변경")
                .seq(3)
                .parent(savedCategory)
                .build();

        Category etcChildCategory = Category.builder()
                .name("기타")
                .seq(4)
                .parent(savedCategory)
                .build();

        return List.of(createChildCategory, deleteChildCategory, updateChildCategory, etcChildCategory);
    }
}
