package com.wrkr.tickety.domains.ticket.application.dto.response.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record StatisticsByCategoryResponse(
    @Schema(description = "통계를 확인하고자 하는 날짜", example = "2025-01-12")
    String date,
    @Schema(description = "부모 카테고리 id", example = "Bqs3C822lkMNdWlmE-szUw", nullable = true)
    String parentCategoryId,
    @Schema(description = "통계 데이터")
    StatisticData statisticData
) {

    @Builder
    public record StatisticData(
        @Schema(description = "2차 카테고리 티켓 수 배열")
        List<TicketCount> firstCategoryTicketCount
    ) {

    }
}

