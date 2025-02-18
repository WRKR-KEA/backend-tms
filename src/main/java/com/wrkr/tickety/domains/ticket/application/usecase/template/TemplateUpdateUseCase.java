package com.wrkr.tickety.domains.ticket.application.usecase.template;

import com.wrkr.tickety.domains.ticket.application.dto.request.template.AdminTemplateUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplatePKResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TemplateMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateGetService;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateUpdateService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
@RequiredArgsConstructor
public class TemplateUpdateUseCase {

    private final TemplateGetService templateGetService;
    private final TemplateUpdateService templateUpdateService;

    public TemplatePKResponse updateTemplate(Long categoryId, AdminTemplateUpdateRequest request) {
        Template template = templateGetService.getTemplateByCategoryId(categoryId);
        Template updatedTemplate = templateUpdateService.update(template, request);
        return TemplateMapper.mapToTemplatePKResponse(updatedTemplate);
    }
}
