package com.wrkr.tickety.domains.member.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "회원 목록 조회 응답")
@Builder
public record MemberInfoPreviewResponse(
    @Schema(description = "Member PK", example = "Bqs3C822lkMNdWlmE-szUw")
    String memberId,

    @Schema(description = "프로필 이미지 URL", example = "https://ibb.co/Gt8fycB")
    String profileImage,

    @Schema(description = "아이디(닉네임)", example = "gachon.kim")
    String nickname,

    @Schema(description = "이름", example = "김가천")
    String name,

    @Schema(description = "부서", example = "백엔드 개발팀")
    String department,

    @Schema(description = "직책", example = "팀장")
    String position,

    @Schema(description = "전화번호", example = "010-1234-5678")
    String phone,

    @Schema(description = "이메일", example = "wrkr@gachon.ac.kr")
    String email

) {

}
