package com.wrkr.tickety.domains.member.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "회원 정보 수정 요청 DTO")
public record MyPageInfoUpdateRequest(
    @NotNull
    @Schema(description = "회원 직급", example = "팀장")
    String position,

    @NotNull
    @Schema(description = "회원 전화번호", example = "010-1234-5678")
    String phone
) {

}
