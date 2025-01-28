package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.ticket.application.dto.response.template.AdminTemplateGetResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.template.TemplateGetUseCase;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.utils.PkCrypto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Template Controller")
@RequestMapping("/api/")
public class AdminTemplateController {

    private final TemplateGetUseCase templateGetUseCase;

    @Operation(summary = "카테고리 ID에 따른 템플릿 조회")
    @GetMapping("/template/{categoryId}")
    @Parameter(name = "categoryId", description = "조회할 템플릿의 카테고리 ID", example = "Gbdsnz3dU0kwFxKpavlkog==", required = true)
    public ApplicationResponse<AdminTemplateGetResponse> getTemplate(@PathVariable String categoryId) {
        AdminTemplateGetResponse template = templateGetUseCase.getTemplate(PkCrypto.decrypt(categoryId));
        return ApplicationResponse.onSuccess(template);
    }



}
