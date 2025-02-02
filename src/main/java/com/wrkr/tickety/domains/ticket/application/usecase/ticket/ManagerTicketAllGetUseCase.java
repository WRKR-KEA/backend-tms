package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.ticket.application.dto.response.ManagerTicketAllGetPagingResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ManagerTicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper;
import com.wrkr.tickety.domains.ticket.domain.constant.SortType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@UseCase
@RequiredArgsConstructor
public class ManagerTicketAllGetUseCase {

    private final MemberGetService memberGetService;
    private final TicketGetService ticketGetService;

    public ManagerTicketAllGetPagingResponse getManagerTicketList(Long managerId, Pageable pageable, TicketStatus status, String search, SortType sortType) {
        memberGetService.byMemberId(managerId);

        Page<Ticket> ticketsPage = ticketGetService.getTicketsByManagerFilter(managerId, pageable, status, search, sortType);

        Page<ManagerTicketAllGetResponse> mappedPage = ticketsPage.map(TicketMapper::toManagerTicketAllGetResponse);

        return ManagerTicketAllGetPagingResponse.from(mappedPage);
    }
}
