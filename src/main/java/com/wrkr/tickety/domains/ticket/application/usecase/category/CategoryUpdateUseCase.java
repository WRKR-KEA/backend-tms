package com.wrkr.tickety.domains.ticket.application.usecase.category;

import static com.wrkr.tickety.domains.ticket.domain.model.Category.updateCategory;

import com.wrkr.tickety.domains.ticket.application.dto.request.Category.CategorySequenceUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryUpdateService;
import com.wrkr.tickety.domains.ticket.application.mapper.CategoryMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@UseCase
@Transactional
@RequiredArgsConstructor
public class CategoryUpdateUseCase {

    private final CategoryGetService categoryGetService;
    private final CategoryUpdateService categoryUpdateService;

    public List<PkResponse> updateCategorySequence(List<CategorySequenceUpdateRequest> request) {
        List<Category> requestUpdatedCategories = request.stream()
            .map(categorySequenceUpdateRequest ->{
                Category findCategory = categoryGetService.getCategory(PkCrypto.decrypt(categorySequenceUpdateRequest.categoryId()))
                    .orElseThrow(() -> ApplicationException.from(CategoryErrorCode.CATEGORY_NOT_EXIST));
                return updateCategory(findCategory, null, categorySequenceUpdateRequest.seq()
                );
            }).toList();

        List<Category> updatedCategories = categoryUpdateService.updateAll(requestUpdatedCategories);
        return CategoryMapper.mapToPkResponseList(updatedCategories);
    }
}
