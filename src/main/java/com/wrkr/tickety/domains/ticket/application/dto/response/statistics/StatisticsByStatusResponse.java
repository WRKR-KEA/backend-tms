package com.wrkr.tickety.domains.ticket.application.dto.response.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "티켓 상태별 개수 조회 응답 DTO", name = "티켓 상태별 개수 조회 응답")
public record StatisticsByStatusResponse(

    @Schema(description = "통계 대상 일/월/연", example = "2025-01-12")
    String date,

    @Schema(description = "요청된 티켓", example = "108")
    Long request,

    @Schema(description = "승인된 티켓", example = "98")
    Long accept,

    @Schema(description = "반려된 티켓", example = "1")
    Long reject,

    @Schema(description = "완료된 티켓", example = "114")
    Long complete
) {

}
