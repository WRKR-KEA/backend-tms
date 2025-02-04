package com.wrkr.tickety.domains.ticket.presentation;

import static com.wrkr.tickety.global.utils.PkCrypto.decrypt;

import com.wrkr.tickety.domains.ticket.application.dto.request.Template.AdminTemplateCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.Template.AdminTemplateUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplateGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplatePKResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.template.TemplateCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.template.TemplateDeleteUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.template.TemplateGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.template.TemplateUpdateUseCase;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.domains.ticket.exception.TemplateErrorCode;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Template Controller")
@RequestMapping("/api/")
public class TemplateController {

    private final TemplateGetUseCase templateGetUseCase;
    private final TemplateCreateUseCase templateCreateUseCase;
    private final TemplateUpdateUseCase templateUpdateUseCase;
    private final TemplateDeleteUseCase templateDeleteUseCase;

    @CustomErrorCodes(templateErrorCodes = {TemplateErrorCode.TEMPLATE_NOT_EXISTS,})
    @Parameter(name = "categoryId", description = "조회할 템플릿의 카테고리 ID", example = "Tqs3C822lkMNdWlmE-szUw", required = true)
    @Operation(summary = "사용자, 관리자 템플릿 조회", description = "사용자 및 관리자가 카테고리 ID에 따른 템플릿을 조회합니다.")
    @GetMapping("user/templates/{categoryId}")
    public ApplicationResponse<TemplateGetResponse> getTemplate(@PathVariable String categoryId) {
        TemplateGetResponse template = templateGetUseCase.getTemplate(decrypt(categoryId));
        return ApplicationResponse.onSuccess(template);
    }

    @CustomErrorCodes(templateErrorCodes = {TemplateErrorCode.TEMPLATE_ALREADY_EXISTS,},
        categoryErrorCodes = {CategoryErrorCode.CATEGORY_NOT_EXISTS})
    @Operation(summary = "관리자 템플릿 추가", description = "관리자가 템플릿을 추가합니다.")
    @PostMapping("admin/templates")
    public ApplicationResponse<TemplatePKResponse> createTemplate(@RequestBody @Valid AdminTemplateCreateRequest request) {
        TemplatePKResponse createdTemplateId = templateCreateUseCase.createTemplate(request);
        return ApplicationResponse.onSuccess(createdTemplateId);
    }

    @CustomErrorCodes(templateErrorCodes = {TemplateErrorCode.TEMPLATE_NOT_EXISTS,})
    @Parameter(name = "categoryId", description = "수정할 템플릿의 카테고리 ID", example = "Tqs3C822lkMNdWlmE-szUw", required = true)
    @Operation(summary = "관리자 템플릿 수정", description = "관리자가 템플릿을 수정합니다.")
    @PatchMapping("admin/templates/{categoryId}")
    public ApplicationResponse<TemplatePKResponse> updateTemplate(@PathVariable String categoryId, @RequestBody @Valid AdminTemplateUpdateRequest request) {
        TemplatePKResponse updatedTemplateId = templateUpdateUseCase.updateTemplate(decrypt(categoryId), request);
        return ApplicationResponse.onSuccess(updatedTemplateId);
    }

    @CustomErrorCodes(templateErrorCodes = {TemplateErrorCode.TEMPLATE_NOT_EXISTS})
    @Parameter(name = "templateId", description = "삭제할 템플릿의 ID", example = "Tqs3C822lkMNdWlmE-szUw", required = true)
    @Operation(summary = "관지라 템플릿 삭제", description = "관리자가 템플릿을 삭제합니다.")
    @DeleteMapping("admin/templates/{templateId}")
    public ApplicationResponse<TemplatePKResponse> deleteTemplate(@PathVariable String templateId) {
        TemplatePKResponse deletedTemplateId = templateDeleteUseCase.deleteTemplate(decrypt(templateId));
        return ApplicationResponse.onSuccess(deletedTemplateId);
    }

}
