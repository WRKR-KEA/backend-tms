package com.wrkr.tickety.domains.ticket.application.usecase.category;

import com.wrkr.tickety.domains.ticket.application.dto.request.category.CategoryCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.CategoryPkResponse.CategoryPK;
import com.wrkr.tickety.domains.ticket.application.mapper.CategoryMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryCreateService;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
@RequiredArgsConstructor
public class CategoryCreateUseCase {

    private final CategoryGetService categoryGetService;
    private final CategoryCreateService categoryCreateService;

    public CategoryPK createCategory(CategoryCreateRequest request) {
        checkCategoryRequestUnique(request.name(), request.abbreviation(), request.seq());

        Category requestCategory = CategoryMapper.mapToCategoryDomain(request);
        Category savedCategory = categoryCreateService.createCategory(requestCategory);
        initChildCategories(savedCategory);
        return CategoryMapper.mapToPkResponse(savedCategory);
    }

    private void checkCategoryRequestUnique(String name, String abbreviation, Integer seq) {
        if (categoryGetService.isCategoryNameExists(name)) {
            throw ApplicationException.from(CategoryErrorCode.CATEGORY_ALREADY_EXISTS);
        }
        if (categoryGetService.isCategoryAbbreviationExists(abbreviation)) {
            throw ApplicationException.from(CategoryErrorCode.CATEGORY_ABBREVIATION_ALREADY_EXISTS);
        }
        if (categoryGetService.isCategorySequenceExists(seq)) {
            throw ApplicationException.from(CategoryErrorCode.CATEGORY_SEQUENCE_ALREADY_EXISTS);
        }
    }

    private void initChildCategories(Category savedCategory) {
        List<Category> children = CategoryMapper.initChildren(savedCategory);
        categoryCreateService.initChildren(children);
    }

}
