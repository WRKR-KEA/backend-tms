package com.wrkr.tickety.domains.ticket.application.usecase.statistics;

import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse.TicketCount;
import com.wrkr.tickety.domains.ticket.application.mapper.StatisticsMapper;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.utils.date.TimePeriodExtractor;
import com.wrkr.tickety.global.utils.date.TimePeriodExtractor.TimePeriod;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsGetUseCase {

    private final TicketHistoryGetService ticketHistoryGetService;

    public StatisticsByTicketStatusResponse getTicketCountStatistics(LocalDateTime dateTime, StatisticsType statisticsType, TicketStatus ticketStatus) {

        TimePeriod timePeriod = TimePeriodExtractor.extractTimePeriod(dateTime, statisticsType);

        List<TicketCount> ticketCountList = ticketHistoryGetService.getTicketCountStatistics(
            timePeriod.getStartDateTime(),
            timePeriod.getEndDateTime(),
            statisticsType,
            ticketStatus
        );

        return StatisticsMapper.mapToStatisticsByTicketStatusResponse(
            dateTime.toString(),
            statisticsType.name(),
            ticketCountList
        );
    }
}
