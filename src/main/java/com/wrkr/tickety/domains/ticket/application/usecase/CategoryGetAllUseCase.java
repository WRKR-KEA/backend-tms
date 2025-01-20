package com.wrkr.tickety.domains.ticket.application.usecase;

import com.wrkr.tickety.domains.ticket.application.dto.response.CategoryGetAllResponseDTO;
import com.wrkr.tickety.domains.ticket.application.mapper.CategoryMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.GuideGetService;
import com.wrkr.tickety.domains.ticket.domain.service.TemplateGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;


@UseCase
@RequiredArgsConstructor
public class CategoryGetAllUseCase {
    private final CategoryGetService categoryGetService;
    private final GuideGetService guideGetService;
    private final TemplateGetService templateGetService;

    public List<CategoryGetAllResponseDTO> getAllCategories() {
        List<Category> categoryList = categoryGetService.byIsDeleted();
        return CategoryMapper.mapToCategoryGetAllResponseDTO(categoryList, guideGetService, templateGetService);
    }


}
