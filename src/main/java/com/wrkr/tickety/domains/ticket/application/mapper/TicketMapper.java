package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.ManagerTicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketDetailGetResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
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
        Member member
    ) {

        return Ticket.builder()
            .user(member)
            .title(ticketCreateRequest.title())
            .content(ticketCreateRequest.content())
            .serialNumber(serialNumber)
            .status(status)
            .category(category)
            .build();
    }

    public static TicketAllGetResponse toTicketAllGetResponse(Ticket ticket, TicketHistoryGetService ticketHistoryGetService) {

        LocalDateTime firstManagerChangeDate = ticketHistoryGetService.getFirstManagerChangeDate(ticket.getTicketId());

        return TicketAllGetResponse.builder()
            .id(PkCrypto.encrypt(ticket.getTicketId()))
            .managerName(ticket.getManager() != null ? ticket.getManager().getNickname() : null)
            .serialNumber(ticket.getSerialNumber())
            .title(ticket.getTitle())
            .status(ticket.getStatus().getDescription())
            .createdAt(ticket.getCreatedAt())
            .startedAt(firstManagerChangeDate)
            .updatedAt(ticket.getUpdatedAt())
            .build();
    }

    public static TicketDetailGetResponse toTicketDetailGetResponse(Ticket ticket, LocalDateTime firstManagerChangeDate) {

        return TicketDetailGetResponse.builder()
                .id(PkCrypto.encrypt(ticket.getTicketId()))
                .title(ticket.getTitle())
                .content(ticket.getContent())
                .status(ticket.getStatus().getDescription())
                .userName(ticket.getUser().getName())
                .managerName(ticket.getManager() != null ? ticket.getManager().getName() : null)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .startedAt(firstManagerChangeDate)
                .build();
    }

    public static ManagerTicketAllGetResponse toManagerTicketAllGetResponse(Ticket ticket) {

        return ManagerTicketAllGetResponse.builder()
            .id(PkCrypto.encrypt(ticket.getTicketId()))
            .serialNumber(ticket.getSerialNumber())
            .title(ticket.getTitle())
            .status(ticket.getStatus().getDescription())
            .requesterName(ticket.getUser().getName())
            .createdAt(ticket.getCreatedAt())
            .updatedAt(ticket.getUpdatedAt())
            .build();
    }
}
