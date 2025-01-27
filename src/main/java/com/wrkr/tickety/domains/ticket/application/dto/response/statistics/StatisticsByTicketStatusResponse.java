package com.wrkr.tickety.domains.ticket.application.dto.response.statistics;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.Builder;

@Builder
public record StatisticsByTicketStatusResponse(
    String targetDate,
    String statisticsType,
    List<TicketCount> countList
) {

    @Builder
    public record TicketCount(
        String date,  // 월 또는 일
        Long count
    ) {

        @QueryProjection
        public TicketCount(String date, Long count) {
            this.date = date;
            this.count = count;
        }
    }
}
