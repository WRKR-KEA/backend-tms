package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketDetailGetResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.TicketHistoryGetService;
import com.wrkr.tickety.global.utils.PkCrypto;

import java.time.LocalDateTime;

public class TicketMapper {

    private TicketMapper() {
        throw new IllegalArgumentException();
    }

    public static Ticket mapToTicket(TicketCreateRequest ticketCreateRequest,
                                     Category category,
                                     String serialNumber,
                                     TicketStatus status,
                                     Member member) {
        return Ticket.builder()
                .user(member)
                .title(ticketCreateRequest.title())
                .content(ticketCreateRequest.content())
                .serialNumber(serialNumber)
                .status(status)
                .category(category)
                .build();
    }
}
