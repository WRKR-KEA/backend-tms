package com.wrkr.tickety.domains.ticket.application.usecase;

import com.wrkr.tickety.domains.ticket.application.dto.response.TicketDetailGetResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketDetailGetUseCase {

    private final TicketGetService ticketGetService;
    private final TicketHistoryGetService ticketHistoryGetService;


    public TicketDetailGetResponse getTicket(Long userId, Long ticketId) {
        Ticket ticket = ticketGetService.getTicketByTicketId(ticketId)
                .orElseThrow(() -> new ApplicationException(TicketErrorCode.TICKET_NOT_FOUND));

        if (!ticket.getUser().getMemberId().equals(userId)) {
            throw new ApplicationException(TicketErrorCode.UNAUTHORIZED_ACCESS);
        }

        LocalDateTime firstManagerChangeDate = ticketHistoryGetService.getFirstManagerChangeDate(ticket.getTicketId());

        return TicketMapper.toTicketDetailGetResponse(ticket, firstManagerChangeDate);
    }
}
