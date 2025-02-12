package com.wrkr.tickety.domains.ticket.application.usecase.category;

import com.wrkr.tickety.domains.ticket.application.dto.response.CategoryPkResponse.CategoryPK;
import com.wrkr.tickety.domains.ticket.application.mapper.CategoryMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryDeleteService;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryUpdateService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;


@UseCase
@Transactional
@RequiredArgsConstructor
public class CategoryDeleteUseCase {

    private final CategoryGetService categoryGetService;
    private final CategoryDeleteService categoryDeleteService;
    private final CategoryUpdateService categoryUpdateService;

    public CategoryPK softDeleteCategory(Long categoryId) {
        Category findCategory = categoryGetService.getParentCategory(categoryId);
        Category deletedCategory = categoryDeleteService.softDeleteCategory(findCategory);
        softDeleteChildCategories(categoryId);
        updateCategorySequence(deletedCategory.getSeq());
        return CategoryMapper.mapToPkResponse(deletedCategory);
    }

    private void softDeleteChildCategories(Long categoryId) {
        List<Category> children = categoryGetService.getChildren(categoryId);
        categoryDeleteService.softDeleteCategories(children);
    }

    private void updateCategorySequence(Integer sequence) {
        List<Category> categories = categoryGetService.findLowerSequenceCategories(sequence);
        categoryUpdateService.updateCategoriesSequenceByDeleted(categories);
    }
}


