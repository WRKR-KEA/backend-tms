package com.wrkr.tickety.domains.ticket.application.usecase.category;

import static com.wrkr.tickety.domains.ticket.domain.model.Category.updateCategory;

import com.wrkr.tickety.domains.ticket.application.dto.request.Category.CategoryNameUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.Category.CategorySequenceUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryUpdateService;
import com.wrkr.tickety.domains.ticket.application.mapper.CategoryMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;


@UseCase
@Transactional
@RequiredArgsConstructor
public class CategoryUpdateUseCase {

    private final CategoryGetService categoryGetService;
    private final CategoryUpdateService categoryUpdateService;

    public List<PkResponse> updateCategorySequence(List<CategorySequenceUpdateRequest> request) {
        List<Category> requestUpdatedCategories = request.stream()
            .map(categorySequenceUpdateRequest ->{
                if(categorySequenceUpdateRequest.seq() == null) throw ApplicationException.from(CategoryErrorCode.CATEGORY_FIELD_CANNOT_NULL);

                Category findCategory = categoryGetService.getCategory(PkCrypto.decrypt(categorySequenceUpdateRequest.categoryId()))
                    .orElseThrow(() -> ApplicationException.from(CategoryErrorCode.CATEGORY_NOT_EXIST));
                return updateCategory(findCategory, null, categorySequenceUpdateRequest.seq()
                );
            }).toList();

        List<Category> updatedCategories = categoryUpdateService.updateAll(requestUpdatedCategories);
        return CategoryMapper.mapToPkResponseList(updatedCategories);
    }

    public PkResponse updateCategoryName(Long categoryId, CategoryNameUpdateRequest request) {
        if(!Objects.equals(categoryId, PkCrypto.decrypt(request.categoryId()))) throw ApplicationException.from(CommonErrorCode.ID_MISMATCH);
        if(request.name().isEmpty()) throw ApplicationException.from(CategoryErrorCode.CATEGORY_FIELD_CANNOT_NULL);

        Category findCategory = categoryGetService.getCategory(categoryId)
            .orElseThrow(() -> ApplicationException.from(CategoryErrorCode.CATEGORY_NOT_EXIST));

        Category updatedCategory = updateCategory(findCategory, request.name(), null);
        Category savedCategory = categoryUpdateService.updateCategoryName(updatedCategory);
        return CategoryMapper.mapToPkResponse(savedCategory);
    }
}
