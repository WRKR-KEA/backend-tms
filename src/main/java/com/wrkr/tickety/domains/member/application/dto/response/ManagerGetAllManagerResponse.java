package com.wrkr.tickety.domains.member.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Schema(description = "담당자 목록 조회 응답 DTO", name = "담당자 목록 조회 응답")
@Builder
public record ManagerGetAllManagerResponse(

    @Schema(description = "담당자 본인", example = "ouqJF8uKst63ZPA2T70jda")
    Managers principal,

    @Schema(description = "담당자 목록")
    List<Managers> managers
) {

    @Builder
    public record Managers(

        @Schema(description = "담당자 ID", example = "ouqJF8uKst63ZPA2T70jda")
        String memberId,

        @Schema(description = "담당자 프로필 사진", example = "알렉스")
        String profileUrl,

        @Schema(description = "담당자 아이디(닉네임)", example = "alex.js")
        String nickname,

        @Schema(description = "담당자 직책", example = "팀장")
        String position,

        @Schema(description = "담당자 이메일", example = "manager123@gachon.ac.kr")
        String email,

        @Schema(description = "담당자 전화번호", example = "010-1234-5678")
        String phoneNumber,

        @Schema(description = "담당 티켓 수", example = "10")
        Long ticketAmount

    ) {

    }
}
