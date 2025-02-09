package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.TicketPinRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketUpdateService;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ManagerTicketPinUseCase {

    private final TicketGetService ticketGetService;
    private final TicketUpdateService ticketUpdateService;

    public TicketPkResponse pinTicket(Member member, TicketPinRequest request) {
        checkPinTicketCountsOverTen(member.getMemberId());

        Ticket requestTicket = ticketGetService.getTicketByTicketId(PkCrypto.decrypt(request.ticketId()));
        Ticket pinnedTicket = ticketUpdateService.pinTicket(requestTicket);

        checkStatusRequested(pinnedTicket);
        checkTicketManager(pinnedTicket, member);
        return TicketMapper.toTicketPkResponse(PkCrypto.encrypt(pinnedTicket.getTicketId()));
    }

    private void checkPinTicketCountsOverTen(Long managerId) {
        if (ticketGetService.countPinTickets(managerId) >= 10) {
            throw new ApplicationException(TicketErrorCode.TICKET_PIN_COUNT_OVER);
        }
    }

    private void checkStatusRequested(Ticket ticket) {
        if (ticket.getManager() == null) {
            throw new ApplicationException(TicketErrorCode.TICKET_MANAGER_NOT_FOUND);
        }
    }

    private void checkTicketManager(Ticket ticket, Member member) {
        if (!ticket.getManager().getMemberId().equals(member.getMemberId())) {
            throw new ApplicationException(TicketErrorCode.TICKET_MANAGER_NOT_MATCH);
        }
    }
}
