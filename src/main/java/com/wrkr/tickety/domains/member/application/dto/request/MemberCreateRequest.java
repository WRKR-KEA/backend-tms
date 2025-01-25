package com.wrkr.tickety.domains.member.application.dto.request;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.presentation.util.annotation.EmailFormat;
import com.wrkr.tickety.domains.member.presentation.util.annotation.ExistEmail;
import com.wrkr.tickety.domains.member.presentation.util.annotation.ExistNickname;
import com.wrkr.tickety.domains.member.presentation.util.annotation.PhoneNumberFormat;
import com.wrkr.tickety.domains.member.presentation.util.annotation.RoleFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Schema(description = "회원 생성 요청 DTO")
@Builder
public record MemberCreateRequest(

    @Schema(description = "이메일", example = "wrkr@gachon.ac.kr")
    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    @EmailFormat
    @ExistEmail
    String email,

    @Schema(description = "이름", example = "김가천")
    @NotBlank(message = "이름은 공백일 수 없습니다.")
    String name,

    @Schema(description = "닉네임", example = "gachon.km")
    @NotBlank(message = "닉네임은 공백일 수 없습니다.")
    @ExistNickname
    String nickname,

    @Schema(description = "부서", example = "백엔드 개발팀")
    @NotBlank(message = "부서는 공백일 수 없습니다.")
    String department,

    @Schema(description = "직책", example = "팀장")
    @NotBlank(message = "직책은 공백일 수 없습니다.")
    String position,

    @Schema(description = "전화번호", example = "010-1234-5678")
    @NotBlank(message = "전화번호는 공백일 수 없습니다.")
    @PhoneNumberFormat
    String phone,

    // TODO: 권한 값 검증 필요(사용자, 담당자, 관리자 중 하나여야함)
    @Schema(description = "회원 역할 (USER | MANAGER | ADMIN)", example = "USER")
    @RoleFormat
    Role role,

    @Schema(description = "프로필 이미지 URL", example = "https://ibb.co/Gt8fycB")
    @NotBlank(message = "프로필 이미지는 공백일 수 없습니다.")
    String profileImage
) {

}
