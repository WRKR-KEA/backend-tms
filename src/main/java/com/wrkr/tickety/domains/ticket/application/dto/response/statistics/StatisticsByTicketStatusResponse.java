package com.wrkr.tickety.domains.ticket.application.dto.response.statistics;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.Builder;

@Builder
public record StatisticsByTicketStatusResponse(
    String baseDate,
    List<TicketCount> countList
) {

    @Builder
    public record TicketCount(
        String targetDate,  // 월 또는 일
        Long count
    ) {

        @QueryProjection
        public TicketCount(String targetDate, Long count) {
            this.targetDate = targetDate;
            this.count = count;
        }
    }
}
