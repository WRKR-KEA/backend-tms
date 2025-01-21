package com.wrkr.tickety.domains.ticket.application.usecase;

import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketHistoryMapper;
import com.wrkr.tickety.domains.ticket.domain.constant.ModifiedType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.model.TicketHistory;
import com.wrkr.tickety.domains.ticket.domain.service.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.TicketHistorySaveService;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class TicketCancelUseCase {

    private final TicketGetService ticketGetService;
    private final TicketHistorySaveService ticketHistorySaveService;

    public PkResponse cancelTicket(Long userId, Long ticketId) {
        Ticket ticket = ticketGetService.getTicketByTicketId(ticketId)
                .orElseThrow(() -> new ApplicationException(TicketErrorCode.TICKET_NOT_FOUND));

        if (!ticket.getUser().getMemberId().equals(userId)) {
            throw new ApplicationException(TicketErrorCode.TICKET_NOT_BELONG_TO_USER);
        }
        if (!ticket.getStatus().equals(TicketStatus.REQUEST)) {
            throw new ApplicationException(TicketErrorCode.TICKET_NOT_REQUEST_STATUS);
        }

        ticket.updateStatus(TicketStatus.CANCEL);

        TicketHistory ticketHistory = TicketHistoryMapper.mapToTicketHistory(ticket, ModifiedType.STATUS);
        ticketHistorySaveService.save(ticketHistory);

        return new PkResponse(PkCrypto.encrypt(ticket.getTicketId()));
    }
}
