package com.wrkr.tickety.domains.ticket.application.usecase.template;

import com.wrkr.tickety.domains.ticket.application.dto.request.Template.AdminTemplateCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplatePKResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TemplateMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateCreateService;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateGetService;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.domains.ticket.exception.TemplateErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
@RequiredArgsConstructor
public class TemplateCreateUseCase {

    private final TemplateGetService templateGetService;
    private final TemplateCreateService templateCreateService;
    private final CategoryGetService categoryGetService;

    public TemplatePKResponse createTemplate(AdminTemplateCreateRequest request) {
        Category category = categoryGetService.getCategory(PkCrypto.decrypt(request.categoryId())).orElseThrow(() -> new ApplicationException(CategoryErrorCode.CATEGORY_NOT_EXISTS));
        if(category.getParent() != null){
            throw new ApplicationException(TemplateErrorCode.TEMPLATE_CANNOT_BE_CREATED_FOR_SUBCATEGORY);
        }
        if(templateGetService.existsByCategoryId(category.getCategoryId())){
            throw new ApplicationException(TemplateErrorCode.TEMPLATE_ALREADY_EXISTS);
        }

        Template requestTemplate = TemplateMapper.mapToTemplateDomain(request, category);
        Template createdTemplate = templateCreateService.save(requestTemplate);
        return TemplateMapper.mapToTemplatePKResponse(createdTemplate);
    }
}
