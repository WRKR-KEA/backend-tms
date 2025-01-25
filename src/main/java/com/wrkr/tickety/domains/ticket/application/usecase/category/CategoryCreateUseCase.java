package com.wrkr.tickety.domains.ticket.application.usecase.category;

import com.wrkr.tickety.domains.ticket.application.dto.request.Category.CategoryCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.CategoryMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryCreateService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
@RequiredArgsConstructor
public class CategoryCreateUseCase {

    private final CategoryCreateService categoryCreateService;

    public PkResponse createCategory(CategoryCreateRequest request) {
        Category requestCategory = CategoryMapper.mapToCategoryDomain(request);
        Category savedCategory = categoryCreateService.createCategory(requestCategory);
        return CategoryMapper.mapToPkResponse(savedCategory);
    }

}
