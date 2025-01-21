package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.domain.constant.ModifiedType;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.model.TicketHistory;

public class TicketHistoryMapper {

    private TicketHistoryMapper() {
        throw new IllegalArgumentException();
    }

    public static TicketHistory mapToTicketHistory(Ticket ticket, ModifiedType modifiedType) {
        return TicketHistory.builder()
            .ticket(ticket)
            .manager(ticket.getManager() != null ? ticket.getManager() : null)
            .status(ticket.getStatus())
            .modified(modifiedType)
            .build();
    }


}
