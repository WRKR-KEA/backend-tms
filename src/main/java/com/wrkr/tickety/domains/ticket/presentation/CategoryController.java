package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.ticket.application.dto.response.CategoryGetAllResponseDTO;
import com.wrkr.tickety.domains.ticket.application.usecase.category.CategoryGetAllUseCase;
import com.wrkr.tickety.global.response.ApplicationResponse;
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

    @GetMapping("/admin/categories")
    public ApplicationResponse<List<CategoryGetAllResponseDTO>> getAllCategories() {
        List<CategoryGetAllResponseDTO> categoryList = categoryGetAllUseCase.getAllCategories();
        return ApplicationResponse.onSuccess(categoryList);
    }

}
