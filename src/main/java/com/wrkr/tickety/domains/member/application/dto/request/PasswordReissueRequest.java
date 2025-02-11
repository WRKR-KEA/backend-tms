package com.wrkr.tickety.domains.member.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record PasswordReissueRequest(

    @Schema(description = "암호화된 회원 PK")
    String memberId,

    @Schema(description = "인증 번호")
    String verificationCode
) {

}
