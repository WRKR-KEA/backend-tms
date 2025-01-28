package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.ticket.application.dto.request.Category.CategoryCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.Category.CategoryNameUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.Category.CategorySequenceUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.category.CategoryGetAllResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.category.CategoryCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.category.CategoryDeleteUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.category.CategoryGetAllUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.category.CategoryUpdateUseCase;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.utils.PkCrypto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Category Controller")
@RequestMapping("/api")
public class CategoryController {

    private final CategoryGetAllUseCase categoryGetAllUseCase;
    private final CategoryCreateUseCase categoryCreateUseCase;
    private final CategoryUpdateUseCase categoryUpdateUseCase;
    private final CategoryDeleteUseCase categoryDeleteUseCase;

    @CustomErrorCodes(categoryErrorCodes = {CategoryErrorCode.CATEGORY_NOT_EXIST})
    @Operation(summary = "카테고리 전체 조회", description = "관리자가 카테고리를 전체 조회합니다.")
    @GetMapping("/admin/categories")
    public ApplicationResponse<List<CategoryGetAllResponse>> getAllCategories() {
        List<CategoryGetAllResponse> categoryList = categoryGetAllUseCase.getAllCategories();
        return ApplicationResponse.onSuccess(categoryList);
    }

    @CustomErrorCodes(categoryErrorCodes = {CategoryErrorCode.CATEGORY_FIELD_CANNOT_NULL,CategoryErrorCode.CATEGORY_ALREADY_EXIST,})
    @Operation(summary = "카테고리 추가", description = "관리자가 카테고리를 추가합니다.")
    @PostMapping("/admin/categories")
    public ApplicationResponse<PkResponse> createCategory(@RequestBody CategoryCreateRequest request){
        PkResponse encryptedCategoryId = categoryCreateUseCase.createCategory(request);
        return ApplicationResponse.onSuccess(encryptedCategoryId);
    }

    @CustomErrorCodes(categoryErrorCodes = {CategoryErrorCode.CATEGORY_NOT_EXIST, CategoryErrorCode.CATEGORY_FIELD_CANNOT_NULL})
    @Operation(summary = "카테고리 순서 수정", description = "관리자가 카테고리의 순서를 수정합니다.")
    @PatchMapping("/admin/categories")
    public ApplicationResponse<List<PkResponse>> updateCategoriesSequence(@RequestBody List<CategorySequenceUpdateRequest> request){
        List<PkResponse> encryptedCategoryId = categoryUpdateUseCase.updateCategorySequence(request);
        return ApplicationResponse.onSuccess(encryptedCategoryId);
    }

    @CustomErrorCodes(categoryErrorCodes = {CategoryErrorCode.CATEGORY_NOT_EXIST, CategoryErrorCode.CATEGORY_ALREADY_EXIST, CategoryErrorCode.CATEGORY_FIELD_CANNOT_NULL})
    @Parameter(name = "categoryId", description = "수정할 카테고리 ID", example = "Gbdsnz3dU0kwFxKpavlkog==", required = true)
    @Operation(summary = "카테고리 이름 수정", description = "관리자가 카테고리 이름을 수정합니다.")
    @PatchMapping("/admin/categories/{categoryId}")
    public ApplicationResponse<PkResponse> updateCategoryName(@PathVariable String categoryId, @RequestBody CategoryNameUpdateRequest request){
        PkResponse encryptedCategoryId = categoryUpdateUseCase.updateCategoryName(PkCrypto.decrypt(categoryId), request);
        return ApplicationResponse.onSuccess(encryptedCategoryId);
    }

    @CustomErrorCodes(categoryErrorCodes = {CategoryErrorCode.CATEGORY_NOT_EXIST, CategoryErrorCode.CATEGORY_ALREADY_DELETED})
    @Parameter(name = "categoryId", description = "수정할 카테고리 ID", example = "Gbdsnz3dU0kwFxKpavlkog==", required = true)
    @Operation(summary = "카테고리 삭제", description = "관리자가 카테고리를 삭제합니다.")
    @DeleteMapping("/admin/categories/{categoryId}")
    public ApplicationResponse<PkResponse> softDeleteCategory(@PathVariable String categoryId){
        PkResponse encryptedCategoryId = categoryDeleteUseCase.softDeleteCategory(PkCrypto.decrypt(categoryId));
        return ApplicationResponse.onSuccess(encryptedCategoryId);
    }

}
