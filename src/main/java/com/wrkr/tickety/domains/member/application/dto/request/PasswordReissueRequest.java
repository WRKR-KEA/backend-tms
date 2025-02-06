package com.wrkr.tickety.domains.member.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordReissueRequest(
    @NotBlank(message = "아이디를 입력해주세요.")
//    @NicknameFormat(message = "유효하지 않은 닉네임입니다.") // TODO: WRKR-118 브랜치 머지되면 적용
    String nickname
) {

}
