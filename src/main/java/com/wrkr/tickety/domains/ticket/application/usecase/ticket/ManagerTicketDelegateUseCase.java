package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper.toTicketPkResponse;

import com.wrkr.tickety.domains.alarm.domain.constant.AgitTicketAlarmMessageType;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.ticket.application.dto.request.TicketDelegateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketHistoryMapper;
import com.wrkr.tickety.domains.ticket.domain.constant.ModifiedType;
import com.wrkr.tickety.domains.ticket.domain.event.TicketStatusChangeEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class ManagerTicketDelegateUseCase {

    private final TicketGetService ticketGetService;
    private final TicketUpdateService ticketUpdateService;
    private final TicketHistorySaveService ticketHistorySaveService;
    private final MemberGetService memberGetService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public TicketPkResponse delegateTicket(Long ticketId, Long currentManagerId, TicketDelegateRequest request) {
        Member delegateManager = memberGetService.byMemberId(PkCrypto.decrypt(request.delegateManagerId()));

        Ticket ticket = ticketGetService.getTicketByTicketId(ticketId);
        validateTicket(ticket, currentManagerId);

        Ticket delegatedTicket = ticketUpdateService.updateManager(ticket, delegateManager);

        TicketHistory ticketHistory = TicketHistoryMapper.mapToTicketHistory(delegatedTicket, ModifiedType.MANAGER);
        ticketHistorySaveService.save(ticketHistory);

        publishTicketDelegateEvent(delegatedTicket);

        return toTicketPkResponse(PkCrypto.encrypt(delegatedTicket.getTicketId()));
    }

    private void publishTicketDelegateEvent(Ticket delegatedTicket) {
        applicationEventPublisher.publishEvent(TicketStatusChangeEvent.builder()
                                                   .ticket(delegatedTicket)
                                                   .user(delegatedTicket.getUser())
                                                   .agitTicketAlarmMessageType(AgitTicketAlarmMessageType.TICKET_DELEGATED_USER)
                                                   .build());
        applicationEventPublisher.publishEvent(TicketStatusChangeEvent.builder()
                                                   .ticket(delegatedTicket)
                                                   .user(delegatedTicket.getUser())
                                                   .agitTicketAlarmMessageType(AgitTicketAlarmMessageType.TICKET_DELEGATE_MANAGER)
                                                   .build());
    }

    private void validateTicket(Ticket ticket, Long currentManagerId) {
        memberGetService.byMemberId(currentManagerId);

        if (!ticket.hasManager()) {
            throw ApplicationException.from(TicketErrorCode.TICKET_MANAGER_NOT_FOUND);
        }

        if (!ticket.isManagedBy(currentManagerId)) {
            throw ApplicationException.from(TicketErrorCode.TICKET_MANAGER_NOT_MATCH);
        }

        if (!ticket.isDelegatable()) {
            throw ApplicationException.from(TicketErrorCode.TICKET_NOT_DELEGATABLE);
        }
    }
}
