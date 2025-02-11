package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.TicketPinRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.ManagerPinTicketResponse;
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

    public ManagerPinTicketResponse pinTicket(Member member, TicketPinRequest request) {
        checkPinTicketCountsOverTen(member.getMemberId(), PkCrypto.decrypt(request.ticketId()));

        Ticket requestTicket = ticketGetService.getTicketByTicketId(PkCrypto.decrypt(request.ticketId()));
        checkStatusRequested(requestTicket);
        checkTicketManager(requestTicket, member);

        Ticket pinnedTicket = ticketUpdateService.pinTicket(requestTicket);
        return TicketMapper.toManagerPinTicketResponse(pinnedTicket);
    }

    private void checkPinTicketCountsOverTen(Long managerId, Long ticketId) {
        if (ticketGetService.countPinTickets(managerId) >= 10 && !ticketGetService.isPinTicket(ticketId)) {
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
