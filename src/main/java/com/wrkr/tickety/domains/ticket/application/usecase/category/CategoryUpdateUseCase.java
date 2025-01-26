package com.wrkr.tickety.domains.ticket.application.usecase.category;

import com.wrkr.tickety.domains.ticket.application.dto.request.Category.CategoryUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
@RequiredArgsConstructor
public class CategoryUpdateUseCase {

    private final CategoryUpdateService categoryUpdateService;

    public PkResponse updateCategory(String encryptedCategoryId, CategoryUpdateRequest request) {



    }
}
