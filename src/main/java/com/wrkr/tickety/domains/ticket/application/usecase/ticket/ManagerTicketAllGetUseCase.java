package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.ticket.application.dto.response.ManagerTicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@UseCase
@RequiredArgsConstructor
public class ManagerTicketAllGetUseCase {

    private final MemberGetService memberGetService;
    private final TicketGetService ticketGetService;

    public ApplicationPageResponse<ManagerTicketAllGetResponse> getManagerTicketList(Long managerId, ApplicationPageRequest pageRequest, TicketStatus status,
        String query) {
        memberGetService.byMemberId(managerId);

        Page<Ticket> ticketsPage = ticketGetService.getTicketsByManagerFilter(managerId, pageRequest, status, query);

        return ApplicationPageResponse.of(ticketsPage, TicketMapper::toManagerTicketAllGetResponse);
    }
}
