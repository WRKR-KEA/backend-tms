package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.ticket.application.dto.request.Template.AdminTemplateCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.template.AdminTemplateGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplatePKResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.template.TemplateCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.template.TemplateGetUseCase;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.domains.ticket.exception.TemplateErrorCode;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.utils.PkCrypto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Template Controller")
@RequestMapping("/api/")
public class AdminTemplateController {

    private final TemplateGetUseCase templateGetUseCase;
    private final TemplateCreateUseCase templateCreateUseCase;

    @CustomErrorCodes(templateErrorCodes = {TemplateErrorCode.TEMPLATE_NOT_EXIST}, categoryErrorCodes = {CategoryErrorCode.CATEGORY_NOT_EXIST})
    @Parameter(name = "categoryId", description = "조회할 템플릿의 카테고리 ID", example = "Gbdsnz3dU0kwFxKpavlkog==", required = true)
    @Operation(summary = "관리자 템플릿 조회", description = "관리자가 카테고리 ID에 따른 템플릿을 조회합니다.")
    @GetMapping("admin/templates/{categoryId}")
    public ApplicationResponse<AdminTemplateGetResponse> getTemplate(@PathVariable String categoryId) {
        AdminTemplateGetResponse template = templateGetUseCase.getTemplate(PkCrypto.decrypt(categoryId));
        return ApplicationResponse.onSuccess(template);
    }

    @CustomErrorCodes(templateErrorCodes = {TemplateErrorCode.TEMPLATE_NOT_EXIST}, categoryErrorCodes = {CategoryErrorCode.CATEGORY_NOT_EXIST})
    @Operation(summary = "템플릿 추가", description = "관리자가 템플릿을 추가합니다.")
    @PostMapping("admin/templates")
    public ApplicationResponse<TemplatePKResponse> createTemplate(@RequestBody AdminTemplateCreateRequest request) {
        TemplatePKResponse createdTemplateId = templateCreateUseCase.createTemplate(request);
        return ApplicationResponse.onSuccess(createdTemplateId);
    }
}
