package com.wrkr.tickety.domains.ticket.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Schema(description = "도움말 수정 요청 DTO", name = "GuideUpdateRequest")
public record GuideUpdateRequest(
    @Schema(description = "수정하고자 하는 도움말 내용", example = "vm 생성 도움말")
    String content,
    @Schema(description = "도움말 첨부파일 URL 목록")
    List<MultipartFile> attachments
) {

}
