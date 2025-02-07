package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.ManagerTicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketDetailGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketPreResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.ManagerTicketDetailResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.ManagerTicketMainPageResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.ManagerTicketMainPageResponse.PinTickets;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.UserTicketMainPageResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;

public class TicketMapper {

    private TicketMapper() {
        throw new IllegalArgumentException();
    }

    public static TicketPkResponse toTicketPkResponse(String ticketId) {
        return TicketPkResponse.builder()
            .ticketId(ticketId)
            .build();
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
            .status(ticket.getStatus())
            .createdAt(ticket.getCreatedAt())
            .startedAt(firstManagerChangeDate)
            .updatedAt(ticket.getUpdatedAt())
            .build();
    }

    public static TicketDetailGetResponse toTicketDetailGetResponse(Ticket ticket, LocalDateTime startDate, LocalDateTime completeDate) {
        return TicketDetailGetResponse.builder()
            .id(PkCrypto.encrypt(ticket.getTicketId()))
            .ticketSerialNumber(ticket.getSerialNumber())
            .title(ticket.getTitle())
            .content(ticket.getContent())
            .category(ticket.getCategory().getParent().getName() + " " + ticket.getCategory().getName())
            .status(ticket.getStatus())
            .userNickname(ticket.getUser().getNickname())
            .managerNickname(ticket.getManager() != null ? ticket.getManager().getNickname() : null)
            .createdAt(ticket.getCreatedAt())
            .updatedAt(ticket.getUpdatedAt())
            .startedAt(startDate)
            .completedAt(completeDate)
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
            .isPinned(ticket.getIsPinned())
            .build();
    }

    public static ManagerTicketDetailResponse toManagerTicketDetailResponse(Ticket ticket, LocalDateTime startDate, LocalDateTime completeDate) {

        return ManagerTicketDetailResponse.builder()
            .ticketId(PkCrypto.encrypt(ticket.getTicketId()))
            .ticketSerialNumber(ticket.getSerialNumber())
            .title(ticket.getTitle())
            .content(ticket.getContent())
            .category(ticket.getCategory().getParent().getName() + " " + ticket.getCategory().getName())
            .userNickname(ticket.getUser().getNickname())
            .managerNickname(ticket.getManager().getNickname())
            .createdAt(ticket.getCreatedAt().format(DateTimeFormatter.ISO_DATE))
            .updatedAt(ticket.getUpdatedAt().format(DateTimeFormatter.ISO_DATE))
            .startedAt(startDate == null ? null : startDate.format(DateTimeFormatter.ISO_DATE))
            .completedAt(completeDate == null ? null : completeDate.format(DateTimeFormatter.ISO_DATE))
            .status(ticket.getStatus())
            .build();
    }

    public static ApplicationPageResponse<DepartmentTicketResponse> toDepartmentTicketResponse(Page<Ticket> ticketPage) {

        return ApplicationPageResponse.of(ticketPage, ticket -> DepartmentTicketResponse.builder()
            .ticketId(PkCrypto.encrypt(ticket.getTicketId()))
            .ticketSerialNumber(ticket.getSerialNumber())
            .status(ticket.getStatus())
            .title(ticket.getTitle())
            .userNickname(ticket.getUser().getNickname())
            .managerNickname(ticket.getManager().getNickname())
            .requestedDate(ticket.getCreatedAt().format(DateTimeFormatter.ISO_DATE))
            .updatedDate(ticket.getUpdatedAt().format(DateTimeFormatter.ISO_DATE))
            .build()
        );
    }

    public static ManagerTicketMainPageResponse toManagerTicketMainPageResponse(List<Ticket> pinTickets, List<Ticket> requestTickets) {
        return ManagerTicketMainPageResponse.builder()
            .pinTickets(pinTickets.stream().map(ticket -> PinTickets.builder()
                .ticketId(PkCrypto.encrypt(ticket.getTicketId()))
                .ticketSerialNumber(ticket.getSerialNumber())
                .status(ticket.getStatus())
                .title(ticket.getTitle())
                .userNickname(ticket.getUser().getNickname())
                .managerNickname(ticket.getManager().getNickname())
                .requestedDate(ticket.getCreatedAt().format(DateTimeFormatter.ISO_DATE))
                .updatedDate(ticket.getUpdatedAt().format(DateTimeFormatter.ISO_DATE))
                .build()
            ).toList())
            .requestTickets(requestTickets.stream().map(ticket -> ManagerTicketMainPageResponse.requestTickets.builder()
                .ticketId(PkCrypto.encrypt(ticket.getTicketId()))
                .ticketSerialNumber(ticket.getSerialNumber())
                .status(ticket.getStatus())
                .title(ticket.getTitle())
                .userNickname(ticket.getUser().getNickname())
                .requestedDate(ticket.getCreatedAt().format(DateTimeFormatter.ISO_DATE))
                .updatedDate(ticket.getUpdatedAt().format(DateTimeFormatter.ISO_DATE))
                .build()
            ).toList())
            .build();
    }

    public static DepartmentTicketResponse mapToDepartmentTicketResponse(DepartmentTicketPreResponse departmentTicketPreResponse) {
        return DepartmentTicketResponse.builder()
            .ticketId(PkCrypto.encrypt(departmentTicketPreResponse.ticketId()))
            .ticketSerialNumber(departmentTicketPreResponse.ticketSerialNumber())
            .status(departmentTicketPreResponse.status())
            .title(departmentTicketPreResponse.title())
            .userNickname(departmentTicketPreResponse.userNickname())
            .managerNickname(departmentTicketPreResponse.managerNickname())
            .requestedDate(departmentTicketPreResponse.requestedDate().format(DateTimeFormatter.ISO_DATE))
            .updatedDate(departmentTicketPreResponse.updatedDate().format(DateTimeFormatter.ISO_DATE))
            .build();
    }

    public static UserTicketMainPageResponse toUserTicketMainPageResponse(
        List<Ticket> recentTickets,
        Map<Long, LocalDateTime> startDatesMap,
        Map<Long, LocalDateTime> completeDatesMap) {

        return UserTicketMainPageResponse.builder()
            .recentTickets(recentTickets.stream().map(ticket -> UserTicketMainPageResponse.recentTickets.builder()
                .ticketId(PkCrypto.encrypt(ticket.getTicketId()))
                .ticketSerialNumber(ticket.getSerialNumber())
                .status(ticket.getStatus())
                .title(ticket.getTitle())
                .userNickname(ticket.getUser().getNickname())
                .managerNickname(ticket.getManager().getNickname() == null ? null : ticket.getManager().getNickname())
                .requestedDate(ticket.getCreatedAt().format(DateTimeFormatter.ISO_DATE))
                .updatedDate(ticket.getUpdatedAt().format(DateTimeFormatter.ISO_DATE))
                .ticketTimeInfo(UserTicketMainPageResponse.recentTickets.ticketTimeInfo.builder()
                    .createdAt(ticket.getCreatedAt())
                    .updatedAt(ticket.getUpdatedAt())
                    .startedAt(startDatesMap.get(ticket.getTicketId()))
                    .endedAt(completeDatesMap.get(ticket.getTicketId()))
                    .build()
                )
                .build()
            ).toList())
            .build();

    }


}
