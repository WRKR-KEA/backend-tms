package com.wrkr.tickety.domains.ticket.application.usecase.template;

import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplatePKResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TemplateMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateDeleteService;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
@RequiredArgsConstructor
public class TemplateDeleteUseCase {

    private final TemplateGetService templateGetService;
    private final TemplateDeleteService templateDeleteService;

    public TemplatePKResponse deleteTemplate(Long templateId) {
        Template template = templateGetService.getTemplateByTemplateId(templateId);
        Template deletedTemplate = templateDeleteService.deleteTemplate(template);
        return TemplateMapper.mapToTemplatePKResponse(deletedTemplate);
    }
}

