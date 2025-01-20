package com.wrkr.tickety.domains.ticket.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GuideCreateRequest {
    @Schema(description = "생성하고자 하는 도움말 내용", example = "vm 생성 도움말")
    private String content;
    @Schema(description = "암호화된 도움말 카테고리 ID", example = "Gbdsnz3dU0kwFxKpavlkog==")
    private String categoryId;
}
