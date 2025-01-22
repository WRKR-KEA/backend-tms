package com.wrkr.tickety.domains.ticket.application.usecase.category;

import com.wrkr.tickety.domains.ticket.application.dto.response.category.CategoryGetAllResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.CategoryMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideGetService;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;


@UseCase
@RequiredArgsConstructor
public class CategoryGetAllUseCase {
    private final CategoryGetService categoryGetService;
    private final GuideGetService guideGetService;
    private final TemplateGetService templateGetService;

    public List<CategoryGetAllResponse> getAllCategories() {
        List<Category> categoryList = categoryGetService.byIsDeleted();
        return CategoryMapper.mapToCategoryGetAllResponseDTO(categoryList, guideGetService, templateGetService);
    }


}
