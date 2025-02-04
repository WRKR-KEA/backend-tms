package com.wrkr.tickety.domains.ticket.application.usecase.category;

import com.wrkr.tickety.domains.ticket.application.dto.response.category.AdminCategoryGetAllResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.CategoryMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideGetService;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;


@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryGetAllUseCase {

    private final CategoryGetService categoryGetService;
    private final GuideGetService guideGetService;
    private final TemplateGetService templateGetService;

    public AdminCategoryGetAllResponse getAllCategories() {
        List<Category> parentCategories = categoryGetService.findParents();

        List<Long> categoryIds = parentCategories.stream()
            .map(Category::getCategoryId)
            .toList();

        Map<Long, Boolean> existsGuideMap = guideGetService.existsByCategoryIds(categoryIds);
        Map<Long, Boolean> existsTemplateMap = templateGetService.existsByCategoryIds(categoryIds);
        List<Category> childCategories = categoryGetService.getChildrenByCategoryIds(categoryIds);

        return CategoryMapper.mapToCategoryGetAllResponseDTO(parentCategories, childCategories, existsGuideMap, existsTemplateMap);
    }
}
