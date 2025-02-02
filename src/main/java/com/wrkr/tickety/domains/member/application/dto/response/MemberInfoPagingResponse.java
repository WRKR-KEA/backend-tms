package com.wrkr.tickety.domains.member.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record MemberInfoPagingResponse(
    @Schema(description = "회원 정보 목록")
    List<MemberInfoResponse> members,

    @Schema(description = "현재 페이지", example = "1")
    int currentPage,

    @Schema(description = "총 페이지 수", example = "10")
    int totalPages,

    @Schema(description = "총 항목 수", example = "100")
    long totalElements,

    @Schema(description = "페이지 크기", example = "10")
    int size
) {

}
