package com.wrkr.tickety.domains.member.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerificationCodeRequest(
    @NotBlank(message = "아이디를 입력해주세요.")
    String nickname
) {

}
