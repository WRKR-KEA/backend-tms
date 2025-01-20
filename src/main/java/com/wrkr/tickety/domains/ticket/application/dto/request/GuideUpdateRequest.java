package com.wrkr.tickety.domains.ticket.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(description = "도움말 수정 요청 DTO", name = "GuideUpdateRequest")
public class GuideUpdateRequest {
    @Schema(description = "수정하고자 하는 도움말 내용", example = "vm 생성 도움말")
    private String content;
}
