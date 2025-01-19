package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.application.dto.request.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;

public class TicketMapper {

    private TicketMapper() {
        throw new IllegalArgumentException();
    }

    public static Ticket mapToTicket(TicketCreateRequest ticketCreateRequest, Category category, String serialNumber, TicketStatus status) {
        return Ticket.builder()
                .title(ticketCreateRequest.title())
                .content(ticketCreateRequest.content())
                .serialNumber(serialNumber)
                .status(status)
                .category(category)
                .build();
    }
}
