package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static com.wrkr.tickety.domains.ticket.application.mapper.TicketHistoryMapper.mapToTicketHistory;
import static com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper.toTicketPkResponse;
import static com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus.IN_PROGRESS;
import static com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus.REJECT;

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
public class TicketRejectUseCase {

    private final TicketGetService ticketGetService;
    private final TicketHistorySaveService ticketHistorySaveService;
    private final TicketUpdateService ticketUpdateService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public TicketPkResponse rejectTicket(Long memberId, String ticketId) {
        Ticket ticket = ticketGetService.getTicketByTicketId(PkCrypto.decrypt(ticketId));
        validateTicketManager(ticket, memberId);
        validateRejectableStatus(ticket);
        Ticket rejectedTicket = ticketUpdateService.updateStatus(ticket, REJECT);

        updateTicketHistory(rejectedTicket);

        applicationEventPublisher.publishEvent(TicketStatusChangeEvent.builder()
            .ticket(rejectedTicket)
            .user(rejectedTicket.getUser())
            .agitTicketNotificationMessageType(AgitTicketNotificationMessageType.TICKET_REJECT)
            .build());

        return toTicketPkResponse(PkCrypto.encrypt(rejectedTicket.getTicketId()));
    }

    private void validateTicketManager(Ticket ticket, Long memberId) {
        if (!ticket.isManagedBy(memberId)) {
            throw ApplicationException.from(TicketErrorCode.TICKET_MANAGER_NOT_MATCH);
        }
    }

    private void validateRejectableStatus(Ticket ticket) {
        if (!ticket.isTicketStatus(IN_PROGRESS)) {
            throw ApplicationException.from(TicketErrorCode.TICKET_NOT_REJECTABLE);
        }
    }

    private void updateTicketHistory(Ticket rejectedTicket) {
        TicketHistory ticketHistory = mapToTicketHistory(rejectedTicket, ModifiedType.STATUS);
        ticketHistorySaveService.save(ticketHistory);
    }
}
