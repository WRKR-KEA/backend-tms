package com.wrkr.tickety.domains.log.application.dto.response;

import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.global.utils.excel.ExcelColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "접속 로그 목록 엑셀 다운로드 DTO")
@Builder
public record AccessLogExcelResponse(

    @Schema(description = "AccessLog PK", example = "Bqs3C822lkMNdWlmE-szUw")
    @ExcelColumn(headerName = "ID")
    String accessLogId,

    @Schema(description = "회원 아이디", example = "gachon.kim")
    @ExcelColumn(headerName = "회원 아이디")
    String nickname,

    @Schema(description = "IP", example = "000.222.444")
    @ExcelColumn(headerName = "IP")
    String ip,

    @Schema(description = "접근 시간", example = "2021-01-01T00:00:00")
    @ExcelColumn(headerName = "접근 시간")
    String accessAt,

    @Schema(description = "접근 유형 (LOGIN | LOGOUT | REFRESH)", example = "LOGIN")
    @ExcelColumn(headerName = "접근 유형")
    ActionType action,

    @Schema(description = "성공 여부 (성공 | 실패)", example = "성공")
    @ExcelColumn(headerName = "상태")
    String isSuccess
) {

}
