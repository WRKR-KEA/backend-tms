package com.wrkr.tickety.domains.member.application.dto.request;

import com.wrkr.tickety.domains.member.presentation.util.annotation.EmailFormat;
import com.wrkr.tickety.domains.member.presentation.util.annotation.PhoneNumberFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "회원 정보 수정 요청 DTO")
public record MyPageInfoUpdateRequest(
    @NotNull
    @Schema(description = "회원 이름", example = "홍길동")
    String name,

    @NotNull
    @PhoneNumberFormat
    @Schema(description = "회원 전화번호", example = "010-1234-5678")
    String phone,

    @Schema(description = "회원 이메일", example = "email@gachon.ac.kr")
    @EmailFormat
    @NotNull
    String email,

    @NotNull
    @Schema(description = "회원 직급", example = "팀장")
    String position,

    @NotNull
    @Schema(description = "회원 아지트 URL", example = "http://agit.com")
    String agitUrl,

    @NotNull
    @Schema(description = "회원 부서", example = "개발팀")
    String department,

    @NotNull
    @Schema(description = "아지트 알림 여부", example = "true")
    Boolean agitNotification,

    @NotNull
    @Schema(description = "이메일 알림 여부", example = "true")
    Boolean emailNotification,

    @NotNull
    @Schema(description = "서비스 알림 여부", example = "true")
    Boolean serviceNotification,

    @NotNull
    @Schema(description = "카카오워크 알림 여부", example = "true")
    Boolean kakaoworkNotification
) {

}
