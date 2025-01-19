package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.application.dto.response.TicketResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;

public class TicketMapper {

    private TicketMapper() {
        throw new IllegalArgumentException();
    }

    public static Ticket mapToUser(TicketResponse ticketResponse) {
        return Ticket.builder()
                // TODO 필드 추가
                .build();
    }
}
