package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse.TicketCount;
import java.util.List;

public class StatisticsMapper {

    public static StatisticsByTicketStatusResponse mapToStatisticsByTicketStatusResponse(
        String targetDate,
        String statisticsType,
        List<TicketCount> ticketCountList
    ) {
        return StatisticsByTicketStatusResponse.builder()
            .targetDate(targetDate)
            .statisticsType(statisticsType)
            .countList(ticketCountList)
            .build();
    }
}
