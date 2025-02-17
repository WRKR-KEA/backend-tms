package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.ManagerTicketMainPageResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ManagerGetMainUseCase {

    private final TicketGetService ticketGetService;

    public ManagerTicketMainPageResponse getMain(Long managerId) {
        List<Ticket> pinTickets = ticketGetService.getPinTickets(managerId);
        List<Ticket> requestTickets = ticketGetService.getRequestTickets();
        return TicketMapper.toManagerTicketMainPageResponse(pinTickets, requestTickets);
    }

    public List<ManagerTicketMainPageResponse.PinTickets> getPinTickets(Long memberId) {
        List<Ticket> pinTickets = ticketGetService.getPinTickets(memberId);
        return pinTickets.stream()
            .map(TicketMapper::toTestPin)
            .toList();
    }

    public List<ManagerTicketMainPageResponse.requestTickets> getRecentTickets() {
        List<Ticket> recentTickets = ticketGetService.getRequestTickets();
        return recentTickets.stream()
            .map(TicketMapper::toTestRequest)
            .toList();
    }
}
