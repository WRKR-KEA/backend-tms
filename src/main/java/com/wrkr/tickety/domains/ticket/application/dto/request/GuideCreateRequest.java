package com.wrkr.tickety.domains.ticket.application.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GuideCreateRequest {
    private String content;
    private Long categoryId;
}
