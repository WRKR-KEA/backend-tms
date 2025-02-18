package com.wrkr.tickety.domains.member.application.dto.request;

import com.wrkr.tickety.domains.member.presentation.util.annotation.PasswordFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "비밀번호 재설정 요청")
@Builder
public record PasswordUpdateRequest(

    @Schema(description = "재설정할 비밀번호", example = "Admin1234!")
    @PasswordFormat
    String password,

    @Schema(description = "비밀번호 확인 필드", example = "Admin1234!")
    @PasswordFormat
    String confirmPassword
) {

}
