package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketAllGetUseCase {

    private final TicketGetService ticketGetService;
    private final TicketHistoryGetService ticketHistoryGetService;
    private final MemberGetService memberGetService;

    public PageResponse<TicketAllGetResponse> getAllTickets(Long userId, Pageable pageable) {
        memberGetService.byMemberId(userId);

        Page<Ticket> ticketsPage = ticketGetService.getTicketsByUserId(userId, pageable);

        return PageResponse.of(ticketsPage, ticket ->
            TicketMapper.toTicketAllGetResponse(ticket, ticketHistoryGetService));
    }
}

