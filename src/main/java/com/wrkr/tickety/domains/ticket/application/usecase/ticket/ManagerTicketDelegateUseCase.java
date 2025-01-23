package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.ticket.application.dto.request.TicketDelegateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketHistoryMapper;
import com.wrkr.tickety.domains.ticket.domain.constant.ModifiedType;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.model.TicketHistory;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketUpdateService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistorySaveService;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class ManagerTicketDelegateUseCase {

    private final TicketGetService ticketGetService;
    private final TicketUpdateService ticketUpdateService;
    private final TicketHistorySaveService ticketHistorySaveService;
    private final MemberGetService memberGetService;

    public PkResponse delegateTicket(Long ticketId, Long currentManagerId,
        TicketDelegateRequest request) {
        Member currentManager = memberGetService.getUserById(currentManagerId)
            .orElseThrow(() -> ApplicationException.from(MemberErrorCode.MEMBER_NOT_FOUND));

        Member delegateManager = memberGetService.getUserById(
                PkCrypto.decrypt(request.delegateManagerId()))
            .orElseThrow(() -> ApplicationException.from(MemberErrorCode.MEMBER_NOT_FOUND));

        Ticket ticket = ticketGetService.getTicketByTicketId(ticketId);

        if (ticket.getManager() == null) {
            throw ApplicationException.from(TicketErrorCode.TICKET_MANAGER_NOT_FOUND);
        }

        if (!ticket.getManager().getMemberId().equals(currentManagerId)) {
            throw ApplicationException.from(TicketErrorCode.TICKET_MANAGER_NOT_MATCH);
        }

        Ticket delegatedTicket = ticketUpdateService.updateManager(ticket, delegateManager);

        TicketHistory ticketHistory = TicketHistoryMapper.mapToTicketHistory(delegatedTicket,
            ModifiedType.MANAGER);
        ticketHistorySaveService.save(ticketHistory);

        return new PkResponse(PkCrypto.encrypt(delegatedTicket.getTicketId()));
    }
}
