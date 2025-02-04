package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static com.wrkr.tickety.domains.ticket.application.mapper.TicketHistoryMapper.mapToTicketHistory;
import static com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper.toTicketPkResponse;

import com.wrkr.tickety.domains.alarm.domain.constant.AgitTicketAlarmMessageType;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
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
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class TicketApproveUseCase {

    private final MemberGetService memberGetService;
    private final TicketGetService ticketGetService;
    private final TicketHistorySaveService ticketHistorySaveService;
    private final TicketUpdateService ticketUpdateService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public List<TicketPkResponse> approveTicket(Long memberId, List<String> ticketIdList) {
        Member member = memberGetService.byMemberId(memberId);
        validateManagerRole(member);

        List<TicketPkResponse> response = new ArrayList<>();

        List<Ticket> approvedTickets = new ArrayList<>();
        for (String ticketId : ticketIdList) {
            Ticket ticket = ticketGetService.getTicketByTicketId(PkCrypto.decrypt(ticketId));
            validateApprovableTicket(ticket);
            Ticket approvedTicket = ticketUpdateService.approveTicket(ticket, member);

            updateTicketHistory(approvedTicket);
            response.add(toTicketPkResponse(PkCrypto.encrypt(approvedTicket.getTicketId())));
            approvedTickets.add(approvedTicket);
        }

        approvedTickets.forEach(ticket -> {
            applicationEventPublisher.publishEvent(TicketStatusChangeEvent.builder()
                                                       .ticket(ticket)
                                                       .user(ticket.getUser())
                                                       .agitTicketAlarmMessageType(AgitTicketAlarmMessageType.TICKET_APPROVED)
                                                       .build());
        });

        return response;
    }

    private void validateManagerRole(Member member) {
        if (!member.isManager()) {
            throw ApplicationException.from(MemberErrorCode.MEMBER_NOT_ALLOWED);
        }
    }

    private void validateApprovableTicket(Ticket ticket) {
        if (!ticket.isApprovable()) {
            throw ApplicationException.from(TicketErrorCode.TICKET_NOT_APPROVABLE);
        }
    }

    private void updateTicketHistory(Ticket approvedTicket) {
        TicketHistory ticketHistory = mapToTicketHistory(approvedTicket, ModifiedType.STATUS);
        ticketHistorySaveService.save(ticketHistory);
    }
}
