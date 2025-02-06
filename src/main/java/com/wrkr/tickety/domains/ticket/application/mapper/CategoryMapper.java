package com.wrkr.tickety.domains.ticket.application.mapper;

import static com.wrkr.tickety.global.utils.PkCrypto.encrypt;

import com.wrkr.tickety.domains.ticket.application.dto.request.category.CategoryCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.CategoryPkResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.category.AdminCategoryGetAllResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.category.UserCategoryGetAllResponse;
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

    public static AdminCategoryGetAllResponse mapToAdminCategoryGetAllResponseDTO(
        List<Category> parentCategories,
        List<Category> childCategories,
        Map<Long, Boolean> existsGuideMap,
        Map<Long, Boolean> existsTemplateMap
    ) {

        return AdminCategoryGetAllResponse.builder()
            .categories(
                parentCategories.stream()
                    .map(category -> AdminCategoryGetAllResponse.categories.builder()
                        .categoryId(encrypt(category.getCategoryId()))
                        .name(category.getName())
                        .seq(category.getSeq())
                        .isExistsGuide(existsGuideMap.get(category.getCategoryId()))
                        .isExistsTemplate(existsTemplateMap.get(category.getCategoryId()))
                        .childCategories(
                            childCategories.stream()
                                .filter(childCategory -> childCategory.getParent().getCategoryId().equals(category.getCategoryId()))
                                .map(childCategory -> AdminCategoryGetAllResponse.categories.childCategories.builder()
                                    .categoryId(encrypt(childCategory.getCategoryId()))
                                    .name(childCategory.getName())
                                    .seq(childCategory.getSeq())
                                    .build()
                                )
                                .toList()
                        )
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

    public static UserCategoryGetAllResponse mapToUserCategoryGetAllResponseDTO(List<Category> parentCategories, List<Category> childCategories) {
        return UserCategoryGetAllResponse.builder()
            .categories(
                parentCategories.stream()
                    .map(category -> UserCategoryGetAllResponse.categories.builder()
                        .categoryId(encrypt(category.getCategoryId()))
                        .name(category.getName())
                        .seq(category.getSeq())
                        .childCategories(
                            childCategories.stream()
                                .filter(childCategory -> childCategory.getParent().getCategoryId().equals(category.getCategoryId()))
                                .map(childCategory -> UserCategoryGetAllResponse.categories.childCategories.builder()
                                    .categoryId(encrypt(childCategory.getCategoryId()))
                                    .name(childCategory.getName())
                                    .seq(childCategory.getSeq())
                                    .build()
                                )
                                .toList()
                        )
                        .build()
                    )
                    .toList()
            )
            .build();

    }
}
