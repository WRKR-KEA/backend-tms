package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.ManagerTicketDetailResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ManagerTicketDetailUseCase {

    private final TicketGetService ticketGetService;
    private final TicketHistoryGetService ticketHistoryGetService;

    public ManagerTicketDetailResponse getManagerTicketDetail(Long ticketId) {

        Ticket ticket = ticketGetService.getTicketByTicketId(ticketId);

        LocalDateTime startDate = ticketHistoryGetService.getStartDate(ticket);
        LocalDateTime completeDate = ticketHistoryGetService.getCompleteDate(ticket);

        return TicketMapper.toManagerTicketDetailResponse(ticket, startDate, completeDate);
    }
}
