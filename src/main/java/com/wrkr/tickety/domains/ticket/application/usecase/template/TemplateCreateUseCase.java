package com.wrkr.tickety.domains.ticket.application.usecase.template;

import static com.wrkr.tickety.global.utils.PkCrypto.decrypt;

import com.wrkr.tickety.domains.ticket.application.dto.request.Template.AdminTemplateCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplatePKResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TemplateMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateCreateService;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateGetService;
import com.wrkr.tickety.domains.ticket.exception.TemplateErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;


@UseCase
@Transactional
@RequiredArgsConstructor
public class TemplateCreateUseCase {

    private final TemplateCreateService templateCreateService;
    private final TemplateGetService templateGetService;
    private final CategoryGetService categoryGetService;

    public TemplatePKResponse createTemplate(AdminTemplateCreateRequest request) {
        Category category = categoryGetService.getParentCategory(decrypt(request.categoryId()));
        checkTemplateExists(category.getCategoryId());

        Template requestTemplate = TemplateMapper.mapToTemplateDomain(request, category);
        Template createdTemplate = templateCreateService.save(requestTemplate);
        return TemplateMapper.mapToTemplatePKResponse(createdTemplate);
    }

    public void checkTemplateExists(Long categoryId) {
        if (templateGetService.existsByCategoryId(categoryId)) {
            throw ApplicationException.from(TemplateErrorCode.TEMPLATE_ALREADY_EXISTS);
        }
    }
}
