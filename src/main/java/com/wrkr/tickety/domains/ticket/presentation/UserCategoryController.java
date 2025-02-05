package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.ticket.application.dto.response.category.UserCategoryGetAllResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.category.CategoryGetAllUseCase;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Category Controller")
@RequestMapping("/api")
public class UserCategoryController {

    private final CategoryGetAllUseCase categoryGetAllUseCase;

    @CustomErrorCodes(categoryErrorCodes = {CategoryErrorCode.CATEGORY_NOT_EXISTS})
    @Operation(summary = "카테고리 전체 조회", description = "사용자가 카테고리를 전체 조회합니다.")
    @GetMapping("/user/categories")
    public ApplicationResponse<UserCategoryGetAllResponse> getAllCategories() {
        UserCategoryGetAllResponse categoryList = categoryGetAllUseCase.userGetAllCategories();
        return ApplicationResponse.onSuccess(categoryList);
    }

}
