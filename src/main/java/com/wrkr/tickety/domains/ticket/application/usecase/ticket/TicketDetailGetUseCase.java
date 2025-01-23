package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.ticket.application.dto.response.TicketDetailGetResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketDetailGetUseCase {

    private final TicketGetService ticketGetService;
    private final TicketHistoryGetService ticketHistoryGetService;


    public TicketDetailGetResponse getTicket(Long userId, Long ticketId) {
        Ticket ticket = ticketGetService.getTicketByTicketId(ticketId);

        if (!ticket.getUser().getMemberId().equals(userId)) {
            throw new ApplicationException(TicketErrorCode.UNAUTHORIZED_ACCESS);
        }

        LocalDateTime firstManagerChangeDate = ticketHistoryGetService.getFirstManagerChangeDate(
            ticket.getTicketId());

        return TicketMapper.toTicketDetailGetResponse(ticket, firstManagerChangeDate);
    }
}
