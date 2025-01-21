package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.application.dto.response.CategoryGetAllResponseDTO;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.Guide.GuideGetService;
import com.wrkr.tickety.domains.ticket.domain.service.TemplateGetService;

import java.util.List;

public class CategoryMapper {

    private CategoryMapper() {throw new IllegalArgumentException();}


    public static List<CategoryGetAllResponseDTO> mapToCategoryGetAllResponseDTO(List<Category> categoryList , GuideGetService guideGetService, TemplateGetService templateGetService) {
        return categoryList.stream()
                .map(category -> {
                    Boolean isExistsGuide = guideGetService.existsByCategoryId(category.getCategoryId());
                    Boolean isExistsTemplate = templateGetService.existsByCategoryId(category.getCategoryId());

                    return new CategoryGetAllResponseDTO(
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
