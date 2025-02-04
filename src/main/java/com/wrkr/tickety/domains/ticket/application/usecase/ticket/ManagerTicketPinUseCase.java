package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.ticket.application.dto.request.Ticket.TicketPinRequest;
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

    public TicketPkResponse pinTicket(TicketPinRequest request) {
        Ticket requestTicket = ticketGetService.getTicketByTicketId(PkCrypto.decrypt(request.ticketId()));
        if(requestTicket.getManager() == null){
            throw new ApplicationException(TicketErrorCode.TICKET_MANAGER_NOT_FOUND);
        }
        if(!requestTicket.getManager().getMemberId().equals(PkCrypto.decrypt(request.managerId()))){
            throw new ApplicationException(TicketErrorCode.UNAUTHORIZED_ACCESS);
        }
        Ticket pinnedTicket = ticketUpdateService.pinTicket(requestTicket);
        return TicketMapper.toTicketPkResponse(PkCrypto.encrypt(pinnedTicket.getTicketId()));
    }
}
