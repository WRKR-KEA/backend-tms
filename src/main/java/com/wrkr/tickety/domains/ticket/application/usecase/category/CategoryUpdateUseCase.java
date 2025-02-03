package com.wrkr.tickety.domains.ticket.application.usecase.category;

import static com.wrkr.tickety.global.utils.PkCrypto.decrypt;

import com.wrkr.tickety.domains.ticket.application.dto.request.Category.CategoryNameUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.Category.CategorySequenceUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.CategoryPkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.CategoryMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryUpdateService;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;


@UseCase
@Transactional
@RequiredArgsConstructor
public class CategoryUpdateUseCase {

    private final CategoryGetService categoryGetService;
    private final CategoryUpdateService categoryUpdateService;

    public CategoryPkResponse updateCategorySequence(List<CategorySequenceUpdateRequest> request) {
        List<Long> categoryIds = request.stream()
            .map(req -> decrypt(req.categoryId()))
            .toList();

        List<Category> findCategories = categoryGetService.getCategories(categoryIds);
        Map<Long, Integer> categoryIdToSeqMap = request.stream()
            .collect(Collectors.toMap(req -> decrypt(req.categoryId()), CategorySequenceUpdateRequest::seq));
        List<Category> updatedCategories = categoryUpdateService.updateCategorySequence(findCategories, categoryIdToSeqMap);

        return CategoryMapper.mapToPkResponseList(updatedCategories);
    }

    public CategoryPkResponse.CategoryPK updateCategoryName(Long categoryId, CategoryNameUpdateRequest request) {
        checkCategoryNameIsUnique(categoryId, request.name());

        Category findCategory = categoryGetService.getParentCategory(categoryId);
        Category savedCategory = categoryUpdateService.updateCategoryName(findCategory, request.name());
        return CategoryMapper.mapToPkResponse(savedCategory);
    }

    private void checkCategoryNameIsUnique(Long categoryId, String name) {
        if (categoryGetService.isCategoryNameExists(categoryId, name)) {
            throw ApplicationException.from(CategoryErrorCode.CATEGORY_ALREADY_EXISTS);
        }
    }
}
