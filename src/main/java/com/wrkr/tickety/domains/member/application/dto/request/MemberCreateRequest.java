package com.wrkr.tickety.domains.member.application.dto.request;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.presentation.util.EmailFormat;
import com.wrkr.tickety.domains.member.presentation.util.PhoneNumberFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record MemberCreateRequest(

        @Schema(description = "이메일", example = "wrkr@gachon.ac.kr")
        @NotBlank(message = "이메일은 공백일 수 없습니다.")
        @EmailFormat
        String email,

        @NotBlank(message = "이름은 공백일 수 없습니다.")
        String name,

        @NotBlank(message = "닉네임은 공백일 수 없습니다.")
        String nickname,

        @NotBlank(message = "직책은 공백일 수 없습니다.")
        String position,

        @NotBlank(message = "전화번호는 공백일 수 없습니다.")
        @PhoneNumberFormat
        String phone,

        Role role,

        @NotBlank(message = "프로필 이미지는 공백일 수 없습니다.")
        String profileImage
) {
}
