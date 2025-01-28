package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.application.dto.response.template.AdminTemplateGetResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.global.utils.PkCrypto;

public class TemplateMapper {

    private TemplateMapper() {
        throw new IllegalArgumentException();
    }

    public static AdminTemplateGetResponse mapToAdminTemplateGetResponse(Template template) {
        return AdminTemplateGetResponse.builder()
                .templateId(PkCrypto.encrypt(template.getTemplateId()))
                .categoryId(PkCrypto.encrypt(template.getCategory().getCategoryId()))
                .content(template.getContent())
                .build();
    }

}
