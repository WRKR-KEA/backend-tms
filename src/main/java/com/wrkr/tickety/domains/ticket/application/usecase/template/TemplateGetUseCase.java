package com.wrkr.tickety.domains.ticket.application.usecase.template;

import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplateGetResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TemplateMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
@RequiredArgsConstructor
public class TemplateGetUseCase {

    private final TemplateGetService templateGetService;

    public TemplateGetResponse getTemplate(Long categoryId) {
        Template template = templateGetService.getTemplateByCategoryId(categoryId);
        return TemplateMapper.mapToAdminTemplateGetResponse(template);
    }
}
