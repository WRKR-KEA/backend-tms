package com.wrkr.tickety.domains.log.application.dto.response;

import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Schema(description = "접속 로그 전체 조회 및 검색 DTO")
@Builder
public record AccessLogSearchResponse(

    @Schema(description = "AccessLog PK", example = "Bqs3C822lkMNdWlmE-szUw")
    String accessLogId,

    @Schema(description = "닉네임", example = "gachon.kim")
    String nickname,

    @Schema(description = "접속 회원 권한 (USER | MANAGER | ADMIN)", example = "USER")
    Role role,

    @Schema(description = "접속 ip", example = "000.222.444")
    String ip,

    @Schema(description = "요청 분류 (LOGIN | LOGOUT | REFRESH)", example = "LOGIN")
    ActionType action,

    @Schema(description = "시도일자", example = "2021-01-01T00:00:00")
    LocalDateTime accessAt,

    @Schema(description = "성공 여부 (true | false)", example = "true")
    Boolean isSuccess
) {

}
