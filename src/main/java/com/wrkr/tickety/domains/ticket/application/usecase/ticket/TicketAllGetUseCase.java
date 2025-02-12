package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketAllGetUseCase {

    private final TicketGetService ticketGetService;
    private final TicketHistoryGetService ticketHistoryGetService;
    private final MemberGetService memberGetService;

    public ApplicationPageResponse<TicketAllGetResponse> getAllTickets(Long userId, ApplicationPageRequest pageRequest, TicketStatus status) {
        memberGetService.byMemberId(userId);

        Page<Ticket> ticketsPage;
        if (status != null) {
            ticketsPage = ticketGetService.getTicketsByUserIdAndStatus(userId, status, pageRequest);
        } else {
            ticketsPage = ticketGetService.getTicketsByUserId(userId, pageRequest);
        }

        return ApplicationPageResponse.of(ticketsPage, ticket ->
            TicketMapper.toTicketAllGetResponse(ticket, ticketHistoryGetService));
    }
}

