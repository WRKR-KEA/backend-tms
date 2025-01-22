package com.wrkr.tickety.domains.member.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "회원 PK 정보 DTO")
@Builder
public record MemberPkResponse(
        @Schema(description = "Member PK", example = "Bqs3C822lkMNdWlmE-szUw")
        String memberId
) {
}
