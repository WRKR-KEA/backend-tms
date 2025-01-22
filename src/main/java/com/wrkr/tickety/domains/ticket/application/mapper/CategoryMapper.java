package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.application.dto.response.category.CategoryGetAllResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideGetService;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateGetService;

import java.util.List;

public class CategoryMapper {

    private CategoryMapper() {throw new IllegalArgumentException();}


    public static List<CategoryGetAllResponse> mapToCategoryGetAllResponseDTO(List<Category> categoryList , GuideGetService guideGetService, TemplateGetService templateGetService) {
        return categoryList.stream()
                .map(category -> {
                    Boolean isExistsGuide = guideGetService.existsByCategoryId(category.getCategoryId());
                    Boolean isExistsTemplate = templateGetService.existsByCategoryId(category.getCategoryId());

                    return new CategoryGetAllResponse(
                            category.getCategoryId(),
                            category.getName(),
                            category.getSeq(),
                            isExistsGuide,
                            isExistsTemplate
                    );
                })
                .toList();
    }
}
