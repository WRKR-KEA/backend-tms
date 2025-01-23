package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.ticket.application.dto.response.ManagerTicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketAllGetPagingResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@UseCase
@RequiredArgsConstructor
public class ManagerTicketAllGetUseCase {

    private final PkCrypto pkCrypto;
    private final MemberGetService memberGetService;
    private final TicketGetService ticketGetService;

    public TicketAllGetPagingResponse getManagerTicketList(String cryptoManagerId, Pageable pageable, String status,String search) {

        Long managerId = pkCrypto.decryptValue(cryptoManagerId);

        memberGetService.getUserById(managerId)
                .orElseThrow(() -> new ApplicationException(MemberErrorCode.MEMBER_NOT_FOUND));

        Page<Ticket> ticketsPage = ticketGetService.getTicketsByManagerFilter(managerId, pageable,status,search);

        Page<ManagerTicketAllGetResponse> mappedPage = ticketsPage.map(TicketMapper::toManagerTicketAllGetResponse);

        return TicketAllGetPagingResponse.from(mappedPage);
    }
}
