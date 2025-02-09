package com.wrkr.tickety.domains.log.application.dto.response;

import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.global.utils.excel.ExcelColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Schema(description = "접속 로그 전체 조회 및 검색 DTO")
@Builder
public record AccessLogSearchResponse(

    @Schema(description = "AccessLog PK", example = "Bqs3C822lkMNdWlmE-szUw")
    @ExcelColumn(headerName = "인덱스")
    String accessLogId,

    @Schema(description = "닉네임", example = "gachon.kim")
    @ExcelColumn(headerName = "닉네임")
    String nickname,

    @Schema(description = "접속 회원 권한 (USER | MANAGER | ADMIN)", example = "USER")
    @ExcelColumn(headerName = "권한")
    Role role,

    @Schema(description = "접속 ip", example = "000.222.444")
    @ExcelColumn(headerName = "접속 IP")
    String ip,

    @Schema(description = "요청 분류 (LOGIN | LOGOUT | REFRESH)", example = "LOGIN")
    @ExcelColumn(headerName = "요청 분류")
    ActionType action,

    @Schema(description = "시도일자", example = "2021-01-01T00:00:00")
    @ExcelColumn(headerName = "시도 일자")
    LocalDateTime accessAt,

    @Schema(description = "성공 여부 (true | false)", example = "true")
    @ExcelColumn(headerName = "성공 여부")
    Boolean isSuccess
) {

}
