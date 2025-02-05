package com.wrkr.tickety.domains.member.application.dto.request;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.presentation.util.annotation.EmailFormat;
import com.wrkr.tickety.domains.member.presentation.util.annotation.ExistEmail;
import com.wrkr.tickety.domains.member.presentation.util.annotation.ExistNickname;
import com.wrkr.tickety.domains.member.presentation.util.annotation.NicknameFormat;
import com.wrkr.tickety.domains.member.presentation.util.annotation.PhoneNumberFormat;
import com.wrkr.tickety.domains.member.presentation.util.annotation.RoleFormat;
import com.wrkr.tickety.global.utils.excel.ExcelColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "회원 생성 요청 DTO")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberCreateRequest {

    @ExcelColumn(headerName = "이메일")
    @Schema(description = "이메일", example = "wrkr@gachon.ac.kr")
    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    @EmailFormat
    @ExistEmail
    String email;

    @ExcelColumn(headerName = "이름")
    @Schema(description = "이름", example = "김가천")
    @NotBlank(message = "이름은 공백일 수 없습니다.")
    String name;

    @ExcelColumn(headerName = "닉네임")
    @Schema(description = "닉네임", example = "gachon.km")
    @NotBlank(message = "닉네임은 공백일 수 없습니다.")
    @ExistNickname
    @NicknameFormat
    String nickname;

    @ExcelColumn(headerName = "부서")
    @Schema(description = "부서", example = "백엔드 개발팀")
    @NotBlank(message = "부서는 공백일 수 없습니다.")
    String department;

    @ExcelColumn(headerName = "직책")
    @Schema(description = "직책", example = "팀장")
    @NotBlank(message = "직책은 공백일 수 없습니다.")
    String position;

    @ExcelColumn(headerName = "전화번호")
    @Schema(description = "전화번호", example = "010-1234-5678")
    @NotBlank(message = "전화번호는 공백일 수 없습니다.")
    @PhoneNumberFormat
    String phone;

    @ExcelColumn(headerName = "역할")
    @Schema(description = "회원 역할 (USER | MANAGER | ADMIN)", example = "USER")
    @RoleFormat
    Role role;

    @ExcelColumn(headerName = "프로필 이미지 URL")
    @Schema(description = "프로필 이미지 URL", example = "https://ibb.co/Gt8fycB")
    String profileImage;
}

