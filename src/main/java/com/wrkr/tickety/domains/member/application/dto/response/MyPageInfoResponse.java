package com.wrkr.tickety.domains.member.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "회원 정보 조회 요청 DTO")
public record MyPageInfoResponse(
    @Schema(description = "회원 PK", example = "MJMzUD7jxcQfUiy3yPMl6A")
    String memberId,

    @Schema(description = "회원 닉네임", example = "alex.js")
    String nickname,

    @Schema(description = "회원 이름", example = "홍길동")
    String name,

    @Schema(description = "회원 전화번호", example = "010-1234-5678")
    String phone,

    @Schema(description = "회원 이메일", example = "email@gachon.ac.kr")
    String email,

    @Schema(description = "회원 직급", example = "팀장")
    String position,

    @Schema(description = "회원 프로필 이미지", example = "http://image.com")
    String profileImage,

    @Schema(description = "회원 권한", example = "담당자")
    String role,

    @Schema(description = "회원 아지트 URL", example = "http://agit.com")
    String agitUrl,

    @Schema(description = "아지트 알림 여부", example = "true")
    Boolean agitNotification,

    @Schema(description = "이메일 알림 여부", example = "true")
    Boolean emailNotification,

    @Schema(description = "서비스 알림 여부", example = "true")
    Boolean serviceNotification
) {

}
