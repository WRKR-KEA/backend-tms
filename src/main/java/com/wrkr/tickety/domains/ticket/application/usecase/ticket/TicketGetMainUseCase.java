package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.UserTicketMainPageResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.model.TicketHistory;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.model.BaseTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class TicketGetMainUseCase {

    private final TicketGetService ticketGetService;
    private final TicketHistoryGetService ticketHistoryGetService;

    public UserTicketMainPageResponse getMain(Long userId) {
        List<Ticket> recentTickets = ticketGetService.getMyRecentTickets(userId);
        List<Long> ticketIds = recentTickets.stream().map(Ticket::getTicketId).toList();
        List<TicketHistory> startDates = ticketHistoryGetService.getStartDates(ticketIds);
        List<TicketHistory> completeDates = ticketHistoryGetService.getCompleteDates(ticketIds);

        Map<Long, LocalDateTime> startDatesMap = getTicketIdToDateTimeMap(startDates);
        Map<Long, LocalDateTime> completeDatesMap = getTicketIdToDateTimeMap(completeDates);
        return TicketMapper.toUserTicketMainPageResponse(recentTickets, startDatesMap, completeDatesMap);
    }

    private Map<Long, LocalDateTime> getTicketIdToDateTimeMap(List<TicketHistory> ticketHistories) {
        return ticketHistories.stream()
            .collect(Collectors.toMap(ticketHistory -> ticketHistory.getTicket().getTicketId(),
                    BaseTime::getCreatedAt
                )
            );
    }
}
