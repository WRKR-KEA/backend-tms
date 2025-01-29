package com.wrkr.tickety.domains.ticket.application.usecase.category;

import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.CategoryMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryDeleteService;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@UseCase
@Transactional
@RequiredArgsConstructor
public class CategoryDeleteUseCase {

    private final CategoryGetService categoryGetService;
    private final CategoryDeleteService categoryDeleteService;

    public PkResponse softDeleteCategory(Long categoryId) {
        Category reqeustCategory = categoryGetService.getCategory(categoryId)
            .orElseThrow(() -> ApplicationException.from(CategoryErrorCode.CATEGORY_NOT_EXISTS));
        if(reqeustCategory.getIsDeleted()) throw ApplicationException.from(CategoryErrorCode.CATEGORY_ALREADY_DELETED);

        List<Category> requestChildren = categoryGetService.getChildren(categoryId);

        List<Category> children = requestChildren.stream()
                .map(Category::softDeleteCategory)
                .toList();
        Category category = Category.softDeleteCategory(reqeustCategory);
        Category deletedCategory = categoryDeleteService.softDeleteCategory(category, children);
        return CategoryMapper.mapToPkResponse(deletedCategory);
    }
}


