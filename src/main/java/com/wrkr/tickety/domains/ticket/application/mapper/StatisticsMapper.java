package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse.TicketCount;
import java.util.List;

public class StatisticsMapper {

    public static StatisticsByTicketStatusResponse mapToStatisticsByTicketStatusResponse(
        String baseDate,
        List<TicketCount> ticketCounts
    ) {
        return StatisticsByTicketStatusResponse.builder()
            .baseDate(baseDate)
            .countList(ticketCounts)
            .build();
    }
}
