package com.wrkr.tickety.domains.ticket.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record StatisticsByCategoryResponse(
    @Schema(description = "통계를 확인하고자 하는 날짜", example = "2025-01-12")
    String date,
    StatisticData statisticData
) {

    @Builder
    public record StatisticData(
        @Schema(description = "1차 카테고리 티켓 수 배열")
        List<TicketCount> firstCategoryTicketCount,
        @Schema(description = "2차 카테고리 티켓 수 배열")
        List<TicketCount> secondCategoryTicketCount
    ) {

    }

    @Builder
    public record TicketCount(
        @Schema(description = "카테고리 id", example = "Bqs3C822lkMNdWlmE-szUw")
        String categoryId,
        @Schema(description = "카테고리 이름", example = "개발")
        String categoryName,
        @Schema(description = "티켓 수", example = "10")
        long count
    ) {

    }
}

