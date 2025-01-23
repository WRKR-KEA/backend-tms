package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.ticket.application.dto.request.TicketDelegateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketHistoryMapper;
import com.wrkr.tickety.domains.ticket.domain.constant.ModifiedType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
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
        Member currentManager = getMemberById(currentManagerId);
        Member delegateManager = getMemberById(PkCrypto.decrypt(request.delegateManagerId()));

        Ticket ticket = ticketGetService.getTicketByTicketId(ticketId);
        validateTicket(ticket, currentManagerId);

        Ticket delegatedTicket = ticketUpdateService.updateManager(ticket, delegateManager);

        TicketHistory ticketHistory = TicketHistoryMapper.mapToTicketHistory(delegatedTicket,
            ModifiedType.MANAGER);
        ticketHistorySaveService.save(ticketHistory);

        return new PkResponse(PkCrypto.encrypt(delegatedTicket.getTicketId()));
    }

    private Member getMemberById(Long memberId) {
        return memberGetService.getUserById(memberId)
            .orElseThrow(() -> ApplicationException.from(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateTicket(Ticket ticket, Long currentManagerId) {
        if (ticket.getManager() == null) {
            throw ApplicationException.from(TicketErrorCode.TICKET_MANAGER_NOT_FOUND);
        }

        if (!ticket.getManager().getMemberId().equals(currentManagerId)) {
            throw ApplicationException.from(TicketErrorCode.TICKET_MANAGER_NOT_MATCH);
        }

        if (ticket.getStatus() != TicketStatus.IN_PROGRESS) {
            throw ApplicationException.from(TicketErrorCode.TICKET_STATUS_NOT_IN_PROGRESS);
        }
    }
}
