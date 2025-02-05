package com.wrkr.tickety.domains.ticket.application.mapper;

import static com.wrkr.tickety.global.utils.PkCrypto.encrypt;

import com.wrkr.tickety.domains.ticket.application.dto.request.category.CategoryCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.CategoryPkResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.category.CategoryGetAllResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import java.util.List;
import java.util.Map;

public class CategoryMapper {

    private CategoryMapper() {
        throw new IllegalArgumentException();
    }

    public static CategoryPkResponse.CategoryPK mapToPkResponse(Category category) {
        return CategoryPkResponse.CategoryPK.builder()
            .categoryId(encrypt(category.getCategoryId()))
            .build();
    }

    public static CategoryPkResponse mapToPkResponseList(List<Category> updatedCategories) {
        return CategoryPkResponse.builder()
            .categories(
                updatedCategories.stream()
                    .map(CategoryMapper::mapToPkResponse)
                    .toList()
            )
            .build();
    }

    public static CategoryGetAllResponse mapToCategoryGetAllResponseDTO(
        List<Category> categoryList,
        Map<Long, Boolean> existsGuideMap,
        Map<Long, Boolean> existsTemplateMap
    ) {

        return CategoryGetAllResponse.builder()
            .categories(
                categoryList.stream()
                    .map(category -> CategoryGetAllResponse.categories.builder()
                        .categoryId(encrypt(category.getCategoryId()))
                        .name(category.getName())
                        .seq(category.getSeq())
                        .isExistsGuide(existsGuideMap.get(category.getCategoryId()))
                        .isExistsTemplate(existsTemplateMap.get(category.getCategoryId()))
                        .build()
                    )
                    .toList()
            )
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
