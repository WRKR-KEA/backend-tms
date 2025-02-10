package com.wrkr.tickety.domains.member.application.dto.request;

import com.wrkr.tickety.domains.member.presentation.util.annotation.NicknameFormat;
import jakarta.validation.constraints.NotBlank;

public record VerificationCodeRequest(
    @NotBlank(message = "아이디를 입력해주세요.")
    @NicknameFormat(message = "유효하지 않은 닉네임입니다.")
    String nickname
) {

}
