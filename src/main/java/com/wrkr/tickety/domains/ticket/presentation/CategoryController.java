package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.Category.CategoryCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.category.CategoryGetAllResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.category.CategoryCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.category.CategoryGetAllUseCase;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Category Controller")
@RequestMapping("/api")
public class CategoryController {

    private final CategoryGetAllUseCase categoryGetAllUseCase;
    private final CategoryCreateUseCase categoryCreateUseCase;

    @Operation(summary = "카테고리 전체 조회", description = "관리자가 카테고리를 전체 조회합니다.")
    @GetMapping("/admin/categories")
    public ApplicationResponse<List<CategoryGetAllResponse>> getAllCategories(@AuthenticationPrincipal Member member) {
        List<CategoryGetAllResponse> categoryList = categoryGetAllUseCase.getAllCategories();
        return ApplicationResponse.onSuccess(categoryList);
    }

    @Operation(summary = "카테고리 추가", description = "관리자가 카테고리를 추가합니다.")
    @PostMapping("/admin/categories")
    public ApplicationResponse<PkResponse> createCategory(@AuthenticationPrincipal Member member, @RequestBody CategoryCreateRequest request) {
        PkResponse encryptedCategoryId = categoryCreateUseCase.createCategory(request);
        return ApplicationResponse.onSuccess(encryptedCategoryId);
    }
}
