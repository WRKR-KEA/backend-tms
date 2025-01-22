package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.ticket.application.dto.response.category.CategoryGetAllResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.category.CategoryGetAllUseCase;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Category Controller")
@RequestMapping("/api")
public class CategoryController {

    private final CategoryGetAllUseCase categoryGetAllUseCase;

    @Operation(summary = "카테고리 전체 조회", description = "관리자가 카테고리를 전체 조회합니다.")
    @GetMapping("/admin/categories")
    public ApplicationResponse<List<CategoryGetAllResponse>> getAllCategories() {
        List<CategoryGetAllResponse> categoryList = categoryGetAllUseCase.getAllCategories();
        return ApplicationResponse.onSuccess(categoryList);
    }

}
