package com.wrkr.tickety.domains.member.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordReissueRequest(
    @NotBlank(message = "회원 PK를 입력해주세요.")
    String memberId,

    @NotBlank(message = "인증 번호를 입력해주세요.")
    String verificationCode
) {

}
