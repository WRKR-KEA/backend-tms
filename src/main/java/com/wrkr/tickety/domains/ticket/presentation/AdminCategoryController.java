package com.wrkr.tickety.domains.ticket.presentation;

import static com.wrkr.tickety.global.utils.PkCrypto.decrypt;

import com.wrkr.tickety.domains.ticket.application.dto.request.category.CategoryCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.category.CategoryNameUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.category.CategorySequenceUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.CategoryPkResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.CategoryPkResponse.CategoryPK;
import com.wrkr.tickety.domains.ticket.application.dto.response.category.AdminCategoryGetAllResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.category.CategoryCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.category.CategoryDeleteUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.category.CategoryGetAllUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.category.CategoryUpdateUseCase;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin Category Controller")
@RequestMapping("/api")
public class AdminCategoryController {

    private final CategoryGetAllUseCase categoryGetAllUseCase;
    private final CategoryCreateUseCase categoryCreateUseCase;
    private final CategoryUpdateUseCase categoryUpdateUseCase;
    private final CategoryDeleteUseCase categoryDeleteUseCase;

    @Operation(summary = "카테고리 전체 조회", description = "관리자가 카테고리를 전체 조회합니다.")
    @GetMapping("/admin/categories")
    public ApplicationResponse<AdminCategoryGetAllResponse> getAllCategories() {
        AdminCategoryGetAllResponse categoryList = categoryGetAllUseCase.adminGetAllCategories();
        return ApplicationResponse.onSuccess(categoryList);
    }

    @CustomErrorCodes(categoryErrorCodes = {CategoryErrorCode.CATEGORY_ALREADY_EXISTS,})
    @Operation(summary = "카테고리 추가", description = "관리자가 카테고리를 추가합니다.")
    @PostMapping("/admin/categories")
    public ApplicationResponse<CategoryPK> createCategory(@RequestBody @Valid CategoryCreateRequest request) {
        CategoryPK encryptedCategoryId = categoryCreateUseCase.createCategory(request);
        return ApplicationResponse.onSuccess(encryptedCategoryId);
    }

    @CustomErrorCodes(categoryErrorCodes = {CategoryErrorCode.CATEGORY_NOT_EXISTS,})
    @Operation(summary = "카테고리 순서 수정", description = "관리자가 카테고리의 순서를 수정합니다.")
    @PatchMapping("/admin/categories")
    public ApplicationResponse<CategoryPkResponse> updateCategoriesSequence(@RequestBody @Valid List<CategorySequenceUpdateRequest> request) {
        CategoryPkResponse encryptedCategoryIds = categoryUpdateUseCase.updateCategorySequence(request);
        return ApplicationResponse.onSuccess(encryptedCategoryIds);
    }

    @CustomErrorCodes(categoryErrorCodes = {CategoryErrorCode.CATEGORY_NOT_EXISTS, CategoryErrorCode.CATEGORY_ALREADY_EXISTS,})
    @Parameter(name = "categoryId", description = "수정할 카테고리 ID", example = "Gbdsnz3dU0kwFxKpavlkog==", required = true)
    @Operation(summary = "카테고리 이름 수정", description = "관리자가 카테고리 이름을 수정합니다.")
    @PatchMapping("/admin/categories/{categoryId}")
    public ApplicationResponse<CategoryPK> updateCategoryName(@PathVariable String categoryId,
        @RequestBody @Valid CategoryNameUpdateRequest request
    ) {
        CategoryPK encryptedCategoryId = categoryUpdateUseCase.updateCategoryName(decrypt(categoryId), request);
        return ApplicationResponse.onSuccess(encryptedCategoryId);
    }

    @CustomErrorCodes(categoryErrorCodes = {CategoryErrorCode.CATEGORY_NOT_EXISTS,})
    @Parameter(name = "categoryId", description = "수정할 카테고리 ID", example = "Gbdsnz3dU0kwFxKpavlkog==", required = true)
    @Operation(summary = "카테고리 삭제", description = "관리자가 카테고리를 삭제합니다.")
    @DeleteMapping("/admin/categories/{categoryId}")
    public ApplicationResponse<CategoryPK> softDeleteCategory(@PathVariable String categoryId) {
        CategoryPK encryptedCategoryId = categoryDeleteUseCase.softDeleteCategory(decrypt(categoryId));
        return ApplicationResponse.onSuccess(encryptedCategoryId);
    }

}
