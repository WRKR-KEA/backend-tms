package com.wrkr.tickety.domains.member.application.dto.request;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.global.utils.excel.ExcelColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "회원 생성 요청 DTO")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberCreateRequestForExcel {

    @ExcelColumn(headerName = "이메일")
    @Schema(description = "이메일", example = "wrkr@gachon.ac.kr")
    String email;

    @ExcelColumn(headerName = "이름")
    @Schema(description = "이름", example = "김가천")
    String name;

    @ExcelColumn(headerName = "닉네임")
    @Schema(description = "닉네임", example = "gachon.km")
    String nickname;

    @ExcelColumn(headerName = "부서")
    @Schema(description = "부서", example = "백엔드 개발팀")
    String department;

    @ExcelColumn(headerName = "직책")
    @Schema(description = "직책", example = "팀장")
    String position;

    @ExcelColumn(headerName = "전화번호")
    @Schema(description = "전화번호", example = "010-1234-5678")
    String phone;

    @ExcelColumn(headerName = "역할")
    @Schema(description = "회원 역할 (USER | MANAGER | ADMIN)", example = "USER")
    Role role;

    @ExcelColumn(headerName = "프로필 이미지 URL")
    @Schema(description = "프로필 이미지 URL", example = "https://i.ibb.co/9V1m6sz/image.jpg")
    String profileImage;

    @ExcelColumn(headerName = "아지트 URL")
    @Schema(description = "아지트 URL", example = "https://example.com/agit")
    String agitUrl;
}

