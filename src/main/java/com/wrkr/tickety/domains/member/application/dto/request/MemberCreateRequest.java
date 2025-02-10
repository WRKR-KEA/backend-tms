package com.wrkr.tickety.domains.member.application.dto.request;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MemberCreateRequest(

    @Schema(description = "이메일", example = "wrkr@gachon.ac.kr")
    String email,

    @Schema(description = "이름", example = "김가천")
    String name,

    @Schema(description = "닉네임", example = "gachon.km")
    String nickname,

    @Schema(description = "부서", example = "백엔드 개발팀")
    String department,

    @Schema(description = "직책", example = "팀장")
    String position,

    @Schema(description = "전화번호", example = "010-1234-5678")
    String phone,

    @Schema(description = "회원 역할 (USER | MANAGER | ADMIN)", example = "USER")
    Role role,

    @Schema(description = "아지트 URL", example = "https://example.com/agit")
    String agitUrl
) {

}
