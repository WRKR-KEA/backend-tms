package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.ticket.application.dto.request.Template.AdminTemplateCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.Template.AdminTemplateUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.template.AdminTemplateGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplatePKResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.template.TemplateCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.template.TemplateDeleteUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.template.TemplateGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.template.TemplateUpdateUseCase;
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
    private final TemplateUpdateUseCase templateUpdateUseCase;
    private final TemplateDeleteUseCase templateDeleteUseCase;

    @CustomErrorCodes(templateErrorCodes = {TemplateErrorCode.TEMPLATE_NOT_EXISTS, TemplateErrorCode.TEMPLATE_CANNOT_DO_FOR_SUBCATEGORY_OR_DELETED})
    @Parameter(name = "categoryId", description = "조회할 템플릿의 카테고리 ID", example = "Tqs3C822lkMNdWlmE-szUw", required = true)
    @Operation(summary = "관리자 템플릿 조회", description = "관리자가 카테고리 ID에 따른 템플릿을 조회합니다.")
    @GetMapping("admin/templates/{categoryId}")
    public ApplicationResponse<AdminTemplateGetResponse> getTemplate(@PathVariable String categoryId) {
        AdminTemplateGetResponse template = templateGetUseCase.getTemplate(PkCrypto.decrypt(categoryId));
        return ApplicationResponse.onSuccess(template);
    }

    @CustomErrorCodes(templateErrorCodes = {TemplateErrorCode.TEMPLATE_ALREADY_EXISTS, TemplateErrorCode.TEMPLATE_CANNOT_DO_FOR_SUBCATEGORY_OR_DELETED})
    @Operation(summary = "관리자 템플릿 추가", description = "관리자가 템플릿을 추가합니다.")
    @PostMapping("admin/templates")
    public ApplicationResponse<TemplatePKResponse> createTemplate(@RequestBody AdminTemplateCreateRequest request) {
        TemplatePKResponse createdTemplateId = templateCreateUseCase.createTemplate(request);
        return ApplicationResponse.onSuccess(createdTemplateId);
    }

    @CustomErrorCodes(templateErrorCodes = {TemplateErrorCode.TEMPLATE_NOT_EXISTS, TemplateErrorCode.TEMPLATE_CANNOT_DO_FOR_SUBCATEGORY_OR_DELETED})
    @Parameter(name = "categoryId", description = "수정할 템플릿의 카테고리 ID", example = "Tqs3C822lkMNdWlmE-szUw", required = true)
    @Operation(summary = "관지라 템플릿 수정", description = "관리자가 템플릿을 추가합니다.")
    @PatchMapping("admin/templates/{categoryId}")
    public ApplicationResponse<TemplatePKResponse> updateTemplate(@PathVariable String categoryId, @RequestBody AdminTemplateUpdateRequest request) {
        TemplatePKResponse updatedTemplateId = templateUpdateUseCase.updateTemplate(PkCrypto.decrypt(categoryId), request);
        return ApplicationResponse.onSuccess(updatedTemplateId);
    }

    @CustomErrorCodes(templateErrorCodes = {TemplateErrorCode.TEMPLATE_NOT_EXISTS})
    @Parameter(name = "templateId", description = "삭제할 템플릿의 ID", example = "Tqs3C822lkMNdWlmE-szUw", required = true)
    @Operation(summary = "관지라 템플릿 삭제", description = "관리자가 템플릿을 삭제합니다.")
    @DeleteMapping("admin/templates/{templateId}")
    public ApplicationResponse<TemplatePKResponse> deleteTemplate(@PathVariable String templateId) {
        TemplatePKResponse deletedTemplateId = templateDeleteUseCase.deleteTemplate(PkCrypto.decrypt(templateId));
        return ApplicationResponse.onSuccess(deletedTemplateId);
    }

}
