package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.ManagerTicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketDetailGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketExcelPreResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketExcelResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketPreResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.ManagerPinTicketResponse;
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
import com.wrkr.tickety.global.utils.date.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;

public class TicketMapper {

    private TicketMapper() {
        throw new IllegalArgumentException();
    }

    public static TicketPkResponse toTicketPkResponse(String ticketId) {
        return TicketPkResponse.builder().ticketId(ticketId).build();
    }

    public static Ticket mapToTicket(TicketCreateRequest ticketCreateRequest, Category category, String serialNumber, TicketStatus status, Member member) {

        return Ticket.builder().user(member).title(ticketCreateRequest.title()).content(ticketCreateRequest.content()).serialNumber(serialNumber).status(status)
            .category(category).build();
    }

    public static TicketAllGetResponse toTicketAllGetResponse(Ticket ticket, TicketHistoryGetService ticketHistoryGetService) {
        LocalDateTime firstManagerChangeDate = ticketHistoryGetService.getFirstManagerChangeDate(ticket.getTicketId());

        return TicketAllGetResponse.builder().id(PkCrypto.encrypt(ticket.getTicketId()))

            .managerName(ticket.getManager() == null ? null : ticket.getManager().getNickname())
            .serialNumber(ticket.getSerialNumber())
            .title(ticket.getTitle())
            .status(ticket.getStatus())
            .firstCategory(ticket.getCategory().getParent().getName())
            .secondCategory(ticket.getCategory().getName())
            .createdAt(DateTimeFormatter.yyyyMMddHHmm(ticket.getCreatedAt()))
            .startedAt(firstManagerChangeDate == null ? null : DateTimeFormatter.yyyyMMddHHmm(firstManagerChangeDate))
            .updatedAt(DateTimeFormatter.yyyyMMddHHmm(ticket.getUpdatedAt()))
            .build();
    }

    public static TicketDetailGetResponse toTicketDetailGetResponse(Ticket ticket, LocalDateTime startDate, LocalDateTime completeDate) {
        return TicketDetailGetResponse.builder().id(PkCrypto.encrypt(ticket.getTicketId())).ticketSerialNumber(ticket.getSerialNumber())
            .title(ticket.getTitle()).content(ticket.getContent())
            .firstCategory(ticket.getCategory().getParent().getName())
            .secondCategory(ticket.getCategory().getName())
            .status(ticket.getStatus()).userNickname(ticket.getUser().getNickname())
            .managerNickname(ticket.getManager() == null ? null : ticket.getManager().getNickname())
            .createdAt(DateTimeFormatter.yyyyMMddHHmm(ticket.getCreatedAt()))
            .updatedAt(DateTimeFormatter.yyyyMMddHHmm(ticket.getUpdatedAt()))
            .startedAt(startDate == null ? null : DateTimeFormatter.yyyyMMddHHmm(startDate))
            .completedAt(completeDate == null ? null : DateTimeFormatter.yyyyMMddHHmm(completeDate))
            .build();
    }

    public static ManagerTicketAllGetResponse toManagerTicketAllGetResponse(Ticket ticket) {
        return ManagerTicketAllGetResponse.builder().id(PkCrypto.encrypt(ticket.getTicketId())).serialNumber(ticket.getSerialNumber()).title(ticket.getTitle())
            .status(ticket.getStatus())
            .firstCategory(ticket.getCategory().getParent().getName())
            .secondCategory(ticket.getCategory().getName())
            .requesterNickname(ticket.getUser().getNickname()).createdAt(DateTimeFormatter.yyyyMMddHHmm(ticket.getCreatedAt()))
            .updatedAt(DateTimeFormatter.yyyyMMddHHmm(ticket.getUpdatedAt())).isPinned(ticket.getIsPinned()).build();
    }

    public static ManagerTicketDetailResponse toManagerTicketDetailResponse(Ticket ticket, LocalDateTime startDate, LocalDateTime completeDate) {

        return ManagerTicketDetailResponse.builder().ticketId(PkCrypto.encrypt(ticket.getTicketId())).ticketSerialNumber(ticket.getSerialNumber())
            .title(ticket.getTitle()).content(ticket.getContent())
            .firstCategory(ticket.getCategory().getParent().getName())
            .secondCategory(ticket.getCategory().getName())
            .userNickname(ticket.getUser().getNickname()).managerNickname(ticket.getManager() == null ? null : ticket.getManager().getNickname())
            .createdAt(DateTimeFormatter.yyyyMMddHHmm(ticket.getCreatedAt())).updatedAt(DateTimeFormatter.yyyyMMddHHmm(ticket.getUpdatedAt()))
            .startedAt(startDate == null ? null : DateTimeFormatter.yyyyMMddHHmm(startDate))
            .completedAt(completeDate == null ? null : DateTimeFormatter.yyyyMMddHHmm(completeDate)).status(ticket.getStatus()).build();
    }

    public static ApplicationPageResponse<DepartmentTicketResponse> toDepartmentTicketResponse(Page<Ticket> ticketPage) {

        return ApplicationPageResponse.of(ticketPage, ticket -> DepartmentTicketResponse.builder().ticketId(PkCrypto.encrypt(ticket.getTicketId()))
            .ticketSerialNumber(ticket.getSerialNumber()).status(ticket.getStatus()).title(ticket.getTitle())
            .firstCategory(ticket.getCategory().getParent().getName())
            .secondCategory(ticket.getCategory().getName())
            .userNickname(ticket.getUser().getNickname())
            .managerNickname(ticket.getManager() == null ? null : ticket.getManager().getNickname())
            .requestedDate(DateTimeFormatter.yyyyMMddHHmm(ticket.getCreatedAt())).updatedDate(DateTimeFormatter.yyyyMMddHHmm(ticket.getUpdatedAt()))
            .build());
    }

    public static ManagerTicketMainPageResponse toManagerTicketMainPageResponse(List<Ticket> pinTickets, List<Ticket> requestTickets) {
        return ManagerTicketMainPageResponse.builder().pinTickets(pinTickets.stream()
                .map(ticket -> PinTickets.builder().ticketId(PkCrypto.encrypt(ticket.getTicketId()))
                    .ticketSerialNumber(ticket.getSerialNumber()).status(ticket.getStatus())
                    .title(ticket.getTitle())
                    .firstCategory(ticket.getCategory().getParent().getName())
                    .secondCategory(ticket.getCategory().getName())
                    .userNickname(ticket.getUser().getNickname())
                    .managerNickname(
                        ticket.getManager() == null ? null : ticket.getManager().getNickname())
                    .requestedDate(DateTimeFormatter.yyyyMMddHHmm(ticket.getCreatedAt()))
                    .updatedDate(DateTimeFormatter.yyyyMMddHHmm(ticket.getUpdatedAt())).build()).toList())
            .requestTickets(requestTickets.stream()
                .map(ticket -> ManagerTicketMainPageResponse.requestTickets.builder().ticketId(PkCrypto.encrypt(ticket.getTicketId()))
                    .ticketSerialNumber(ticket.getSerialNumber()).status(ticket.getStatus()).title(ticket.getTitle())
                    .firstCategory(ticket.getCategory().getParent().getName())
                    .secondCategory(ticket.getCategory().getName())
                    .userNickname(ticket.getUser().getNickname()).requestedDate(DateTimeFormatter.yyyyMMddHHmm(ticket.getCreatedAt()))
                    .updatedDate(DateTimeFormatter.yyyyMMddHHmm(ticket.getUpdatedAt())).build()).toList()).build();
    }

    public static DepartmentTicketResponse mapToDepartmentTicketResponse(DepartmentTicketPreResponse preResponse) {
        return DepartmentTicketResponse.builder().ticketId(PkCrypto.encrypt(preResponse.ticketId()))
            .ticketSerialNumber(preResponse.ticketSerialNumber()).status(preResponse.status())
            .title(preResponse.title())
            .firstCategory(preResponse.firstCategory())
            .secondCategory(preResponse.secondCategory())
            .userNickname(preResponse.userNickname())
            .managerNickname(preResponse.managerNickname())
            .requestedDate(DateTimeFormatter.yyyyMMddHHmm(preResponse.requestedDate()))
            .updatedDate(DateTimeFormatter.yyyyMMddHHmm(preResponse.updatedDate())).build();
    }

    public static DepartmentTicketExcelResponse mapToDepartmentTicketExcelResponse(DepartmentTicketExcelPreResponse preResponse) {
        return DepartmentTicketExcelResponse.builder()
            .ticketSerialNumber(preResponse.ticketSerialNumber())
            .status(preResponse.status().getDescription())
            .title(preResponse.title())
            .firstCategory(preResponse.firstCategory())
            .secondCategory(preResponse.secondCategory())
            .userNickname(preResponse.userNickname())
            .managerNickname(preResponse.managerNickname())
            .requestedDate(DateTimeFormatter.yyyyMMddHHmm(preResponse.requestedDate()))
            .updatedDate(DateTimeFormatter.yyyyMMddHHmm(preResponse.updatedDate()))
            .build();
    }

    public static UserTicketMainPageResponse toUserTicketMainPageResponse(List<Ticket> recentTickets, Map<Long, LocalDateTime> startDatesMap,
        Map<Long, LocalDateTime> completeDatesMap) {

        return UserTicketMainPageResponse.builder().recentTickets(recentTickets.stream().map(ticket -> UserTicketMainPageResponse.recentTickets.builder()
                .ticketId(PkCrypto.encrypt(ticket.getTicketId())).ticketSerialNumber(ticket.getSerialNumber()).status(ticket.getStatus())
                .title(ticket.getTitle())
                .firstCategory(ticket.getCategory().getParent().getName())
                .secondCategory(ticket.getCategory().getName())
                .userNickname(ticket.getUser().getNickname()).managerNickname(ticket.getManager() == null ? null : ticket.getManager().getNickname())
                .requestedDate(DateTimeFormatter.yyyyMMddHHmm(ticket.getCreatedAt())).updatedDate(DateTimeFormatter.yyyyMMddHHmm(ticket.getUpdatedAt()))
                .ticketTimeInfo(
                    UserTicketMainPageResponse.recentTickets.ticketTimeInfo.builder()
                        .createdAt(DateTimeFormatter.yyyyMMddHHmm(ticket.getCreatedAt()))
                        .updatedAt(DateTimeFormatter.yyyyMMddHHmm(ticket.getUpdatedAt()))
                        .startedAt(startDatesMap.containsKey(ticket.getTicketId()) ? DateTimeFormatter.yyyyMMddHHmm(
                            startDatesMap.get(ticket.getTicketId())) : null)
                        .endedAt(
                            completeDatesMap.containsKey(ticket.getTicketId()) ? DateTimeFormatter.yyyyMMddHHmm(
                                completeDatesMap.get(ticket.getTicketId())) : null)
                        .build())
                .build())
            .toList()).build();

    }


    public static ManagerPinTicketResponse toManagerPinTicketResponse(Ticket pinnedTicket) {
        return ManagerPinTicketResponse.builder().ticketId(PkCrypto.encrypt(pinnedTicket.getTicketId())).isPinned(pinnedTicket.getIsPinned()).build();
    }

    public static ManagerTicketMainPageResponse.PinTickets toTestPin(Ticket ticket) {
        return ManagerTicketMainPageResponse.PinTickets.builder().ticketId(PkCrypto.encrypt(ticket.getTicketId()))
            .ticketSerialNumber(ticket.getSerialNumber()).status(ticket.getStatus()).title(ticket.getTitle())
            .firstCategory(ticket.getCategory().getParent().getName())
            .secondCategory(ticket.getCategory().getName())
            .userNickname(ticket.getUser().getNickname())
            .managerNickname(ticket.getManager() == null ? null : ticket.getManager().getNickname())
            .requestedDate(DateTimeFormatter.yyyyMMddHHmm(ticket.getCreatedAt()))
            .updatedDate(DateTimeFormatter.yyyyMMddHHmm(ticket.getUpdatedAt())).build();
    }

    public static ManagerTicketMainPageResponse.requestTickets toTestRequest(Ticket ticket) {
        return ManagerTicketMainPageResponse.requestTickets.builder().ticketId(PkCrypto.encrypt(ticket.getTicketId()))
            .ticketSerialNumber(ticket.getSerialNumber()).status(ticket.getStatus()).title(ticket.getTitle())
            .firstCategory(ticket.getCategory().getParent().getName())
            .secondCategory(ticket.getCategory().getName())
            .userNickname(ticket.getUser().getNickname())
            .requestedDate(DateTimeFormatter.yyyyMMddHHmm(ticket.getCreatedAt()))
            .updatedDate(DateTimeFormatter.yyyyMMddHHmm(ticket.getUpdatedAt())).build();
    }
}
