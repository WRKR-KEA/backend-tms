package com.wrkr.tickety.domains.member.application.dto.request;

import com.wrkr.tickety.domains.member.presentation.util.annotation.EmailFormat;
import com.wrkr.tickety.domains.member.presentation.util.annotation.NicknameFormat;
import com.wrkr.tickety.domains.member.presentation.util.annotation.PhoneNumberFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Schema(description = "관리자 - 회원 정보 수정 DTO")
@Builder
public record MemberInfoUpdateRequest(

    @Schema(description = "이메일", example = "wrkr@gachon.ac.kr")
    @EmailFormat(acceptedDomains = {"gachon.ac.kr", "gmail.com"})
    @Size(max = 50, message = "이메일은 50자 이하로 입력해주세요.")
    String email,

    @Schema(description = "이름", example = "김가천")
    @NotBlank(message = "유효하지 않은 이름입니다.")
    @Size(max = 50, message = "이름은 50자 이하로 입력해주세요.")
    String name,

    @Schema(description = "닉네임", example = "gachon.km")
    @NicknameFormat
    @Size(max = 50, message = "아이디는 50자 이하로 입력해주세요.")
    String nickname,

    @Schema(description = "부서", example = "백엔드 개발팀")
    @NotBlank(message = "유효하지 않은 부서입니다.")
    @Size(max = 50, message = "부서는 50자 이하로 입력해주세요.")
    String department,

    @Schema(description = "직책", example = "팀장")
    @NotBlank(message = "유효하지 않은 직책입니다.")
    @Size(max = 50, message = "직책은 50자 이하로 입력해주세요.")
    String position,

    @Schema(description = "전화번호", example = "010-1234-5678")
    @PhoneNumberFormat
    @Size(max = 50, message = "전화번호는 50자 이하로 입력해주세요.")
    String phone,

    @Schema(description = "아지트 URL", example = "https://ibb.co/Gt8fycB")
    String agitUrl
) {

}
