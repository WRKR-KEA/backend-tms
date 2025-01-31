package com.wrkr.tickety.domains.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 DTO")
public record LoginRequest(

    @Schema(description = "닉네임", example = "alex.js")
    @NotBlank(message = "아이디(닉네임)는 필수입니다.")
    String nickname,

    @Schema(description = "비밀번호", example = "Password12!@")
    @NotBlank(message = "비밀번호는 필수입니다.")
    String password
) {

}
