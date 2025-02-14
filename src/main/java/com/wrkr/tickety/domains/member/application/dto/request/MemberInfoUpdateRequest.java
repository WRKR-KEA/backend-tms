package com.wrkr.tickety.domains.member.application.dto.request;

import com.wrkr.tickety.domains.member.presentation.util.annotation.EmailFormat;
import com.wrkr.tickety.domains.member.presentation.util.annotation.NicknameFormat;
import com.wrkr.tickety.domains.member.presentation.util.annotation.PhoneNumberFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Schema(description = "관리자 - 회원 정보 수정 DTO")
@Builder
public record MemberInfoUpdateRequest(

    @Schema(description = "이메일", example = "wrkr@gachon.ac.kr")
    @EmailFormat(acceptedDomains = {"gachon.ac.kr", "gmail.com"})
    String email,

    @Schema(description = "이름", example = "김가천")
    @NotBlank(message = "유효하지 않은 이름입니다.")
    String name,

    @Schema(description = "닉네임", example = "gachon.km")
    @NicknameFormat
    String nickname,

    @Schema(description = "부서", example = "백엔드 개발팀")
    @NotBlank(message = "유효하지 않은 부서입니다.")
    String department,

    @Schema(description = "직책", example = "팀장")
    @NotBlank(message = "유효하지 않은 직책입니다.")
    String position,

    @Schema(description = "전화번호", example = "010-1234-5678")
    @PhoneNumberFormat
    String phone,

    @Schema(description = "아지트 URL", example = "https://ibb.co/Gt8fycB")
    String agitUrl
) {

}
