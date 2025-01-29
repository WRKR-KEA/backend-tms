package com.wrkr.tickety.domains.ticket.application.usecase.template;

import com.wrkr.tickety.domains.ticket.application.dto.request.Template.AdminTemplateUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplatePKResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TemplateMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateGetService;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateUpdateService;
import com.wrkr.tickety.domains.ticket.exception.TemplateErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
@RequiredArgsConstructor
public class TemplateUpdateUseCase {

    private final TemplateGetService templateGetService;
    private final TemplateUpdateService templateUpdateService;

    public TemplatePKResponse updateTemplate(Long categoryId, AdminTemplateUpdateRequest request) {
        if(!categoryId.equals(PkCrypto.decrypt(request.categoryId()))){
            throw new ApplicationException(CommonErrorCode.ID_MISMATCH);
        }

        if(!templateGetService.existsByCategoryId(categoryId)){
            throw new ApplicationException(TemplateErrorCode.TEMPLATE_NOT_EXISTS);
        }

        Template template = templateGetService.getTemplateByCategoryId(categoryId);
        Template requestTemplate = TemplateMapper.updateTemplateDomain(template, request);
        Template updatedTemplate = templateUpdateService.update(requestTemplate);
        return TemplateMapper.mapToTemplatePKResponse(updatedTemplate);
    }
}
