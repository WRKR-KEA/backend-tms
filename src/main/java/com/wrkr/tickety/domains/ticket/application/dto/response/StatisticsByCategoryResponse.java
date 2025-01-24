package com.wrkr.tickety.domains.ticket.application.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record StatisticsByCategoryResponse(
    String date,
    StatisticData statisticData
) {

    @Builder
    public record StatisticData(
        List<TicketCount> FirstCategoryTicketCount,
        List<TicketCount> SecondCategoryTicketCount
    ) {

    }

    @Builder
    public record TicketCount(
        String categoryId,
        String categoryName,
        long count
    ) {

    }
}

