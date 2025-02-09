package com.wrkr.tickety.domains.ticket.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
@Schema(description = "도움말 응답 DTO", name = "GuideResponse")
public record GuideResponse(
    @Schema(description = "도움말 내용", example = "vm 생성 도움말")
    String content,
    @Schema(description = "암호화된 도움말 ID", example = "Gbdsnz3dU0kwFxKpavlkog==")
    String guideId,
    @Schema(description = "도움말 첨부파일 URL 목록")
    List<String> attachmentUrls
) {

}
