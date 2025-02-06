package com.wrkr.tickety.domains.ticket.application.usecase.statistics;

import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByStatusResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.utils.date.DateUtil;
import com.wrkr.tickety.global.utils.date.TimePeriod;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsByStatusUseCase {


    private final TicketHistoryGetService ticketHistoryGetService;
    private final TicketGetService ticketGetService;

    public StatisticsByStatusResponse getStatisticsByStatus(StatisticsType type, String date) {
        LocalDate localDate = DateUtil.convertToLocalDate(date);
        TimePeriod timePeriod = DateUtil.extractTimePeriod(localDate.atStartOfDay(), type);

        return StatisticsByStatusResponse.builder()
            .date(date)
            .request(ticketGetService.countTicketsCreatedBetween(timePeriod.getStartDateTime(), timePeriod.getEndDateTime()))
            .accept(ticketHistoryGetService.getStatisticsByStatus(TicketStatus.IN_PROGRESS, timePeriod.getStartDateTime(), timePeriod.getEndDateTime()))
            .reject(ticketHistoryGetService.getStatisticsByStatus(TicketStatus.REJECT, timePeriod.getStartDateTime(), timePeriod.getEndDateTime()))
            .complete(ticketHistoryGetService.getStatisticsByStatus(TicketStatus.COMPLETE, timePeriod.getStartDateTime(), timePeriod.getEndDateTime()))
            .build();
    }
}
