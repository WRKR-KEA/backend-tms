package com.wrkr.tickety.domains.auth.application.dto.response;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "JWT 토큰 응답 DTO")
@Builder
public record AuthTokenResponse(

    @Schema(description = "엑세스 토큰", example = "eyJ0eXAiOiJKV1QiLCJhbG.eyJzdWIiOiIxQiOjE2OTgzODk0MjAsImV4cCI6MTY5OTU5OTAyMH0.XYB0Bl6UZ3xGgG1wnXd-ACE")
    String accessToken,

    @Schema(description = "리프레시 토큰", example = "eyJ0eXAiOiJKV1QiLCJhbG.eyJzdWIiOiIxQiOjE2OTgzODk0MjAsImV4cCI6MTY5OTU5OTAyMH0.XYB0Bl6UZ3xGgG1wnXd-REF")
    String refreshToken,

    @Schema(description = "닉네임", example = "user.kgc")
    String nickname,

    @Schema(description = "이름", example = "김가천")
    String name,

    @Schema(description = "권한(USER: 사용자 | MANAGER: 담당자 | ADMIN: 관리자)", example = "USER")
    Role role,

    @Schema(description = "프로필 이미지 URL", example = "https://ibb.co/Gt8fycB")
    String profileImage,

    @Schema(description = "임시 비밀번호 여부", example = "false")
    boolean isTempPassword
) {

}
