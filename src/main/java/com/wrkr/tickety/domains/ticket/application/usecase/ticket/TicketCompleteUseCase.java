package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static com.wrkr.tickety.domains.ticket.application.mapper.TicketHistoryMapper.mapToTicketHistory;
import static com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper.toTicketPkResponse;
import static com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus.COMPLETE;
import static com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus.IN_PROGRESS;

import com.wrkr.tickety.domains.notification.domain.constant.agit.AgitTicketNotificationMessageType;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
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
public class TicketCompleteUseCase {

    private final TicketGetService ticketGetService;
    private final TicketHistorySaveService ticketHistorySaveService;
    private final TicketUpdateService ticketUpdateService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public TicketPkResponse completeTicket(Long memberId, Long ticketId) {
        Ticket ticket = ticketGetService.getTicketByTicketId(ticketId);
        validateTicketManager(ticket, memberId);
        validateCompletableStatus(ticket);
        Ticket completedTicket = ticketUpdateService.updateStatus(ticket, COMPLETE);

        updateTicketHistory(completedTicket);

        applicationEventPublisher.publishEvent(TicketStatusChangeEvent.builder()
            .ticket(completedTicket)
            .user(completedTicket.getUser())
            .agitTicketNotificationMessageType(AgitTicketNotificationMessageType.TICKET_FINISHED)
            .build());

        return toTicketPkResponse(PkCrypto.encrypt(completedTicket.getTicketId()));
    }

    private void validateTicketManager(Ticket ticket, Long memberId) {
        if (!ticket.isManagedBy(memberId)) {
            throw ApplicationException.from(TicketErrorCode.TICKET_MANAGER_NOT_MATCH);
        }
    }

    private void validateCompletableStatus(Ticket ticket) {
        if (!ticket.isTicketStatus(IN_PROGRESS)) {
            throw ApplicationException.from(TicketErrorCode.TICKET_NOT_COMPLETABLE);
        }
    }

    private void updateTicketHistory(Ticket rejectedTicket) {
        TicketHistory ticketHistory = mapToTicketHistory(rejectedTicket, ModifiedType.STATUS);
        ticketHistorySaveService.save(ticketHistory);
    }
}
