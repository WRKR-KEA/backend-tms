package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.application.dto.request.template.AdminTemplateCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.template.AdminTemplateGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplatePKResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.global.utils.PkCrypto;

public class TemplateMapper {

    private TemplateMapper() {
        throw new IllegalArgumentException();
    }

    public static TemplatePKResponse mapToTemplatePKResponse(Template template) {
        return TemplatePKResponse.builder()
            .templateId(PkCrypto.encrypt(template.getTemplateId()))
            .build();
    }

    public static AdminTemplateGetResponse mapToAdminTemplateGetResponse(Template template) {
        return AdminTemplateGetResponse.builder()
            .templateId(PkCrypto.encrypt(template.getTemplateId()))
            .categoryId(PkCrypto.encrypt(template.getCategory().getCategoryId()))
            .content(template.getContent())
            .build();
    }

    public static Template mapToTemplateDomain(AdminTemplateCreateRequest request, Category category) {
        return Template.builder()
            .category(category)
            .content(request.content())
            .build();
    }
}
