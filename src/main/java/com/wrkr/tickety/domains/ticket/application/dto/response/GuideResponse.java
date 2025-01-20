package com.wrkr.tickety.domains.ticket.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 도움말 조회 요청 반환 DTO
 */
@Getter
@Builder
@Schema(description = "도움말 응답 DTO", name = "GuideResponse")
public class GuideResponse {
    @Schema(description = "도움말 내용", example = "vm 생성 도움말")
    private String content;
    @Schema(description = "도움말 카테고리 ID", example = "1")
    private String categoryId;
    @Schema(description = "암호화된 도움말 ID", example = "Gbdsnz3dU0kwFxKpavlkog==")
    private String guideId;
}
