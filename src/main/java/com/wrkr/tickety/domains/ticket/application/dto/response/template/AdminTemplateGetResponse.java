package com.wrkr.tickety.domains.ticket.application.dto.response.template;

import lombok.Builder;

@Builder
public record AdminTemplateGetResponse(

            String templateId,
            String categoryId,
            String content
) {
}
