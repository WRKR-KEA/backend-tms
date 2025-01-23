package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper.mapToTicket;
import static com.wrkr.tickety.global.utils.PkCrypto.decrypt;
import static com.wrkr.tickety.global.utils.PkCrypto.encrypt;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.ticket.application.dto.request.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketHistoryMapper;
import com.wrkr.tickety.domains.ticket.domain.constant.ModifiedType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.model.TicketHistory;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketSaveService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistorySaveService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class TicketCreateUseCase {

    private final TicketSaveService ticketSaveService;
    private final CategoryGetService categoryGetService;
    private final MemberGetService UserGetService;
    private final TicketHistorySaveService ticketHistorySaveService;

    public PkResponse createTicket(TicketCreateRequest request, Long userId) {
        Category category = categoryGetService.getCategory(decrypt(request.categoryId()));

        Member member = UserGetService.getUserById(userId)
            .orElseThrow(() -> new ApplicationException(MemberErrorCode.MEMBER_NOT_FOUND));

        String serialNumber = generateSerialNumber();
        TicketStatus status = TicketStatus.REQUEST;

        Ticket ticket = mapToTicket(request, category, serialNumber, status, member);
        Ticket savedTicket = ticketSaveService.save(ticket);

        ModifiedType modifiedType = ModifiedType.STATUS;
        TicketHistory ticketHistory = TicketHistoryMapper.mapToTicketHistory(savedTicket,
            modifiedType);
        ticketHistorySaveService.save(ticketHistory);

        return new PkResponse(encrypt(savedTicket.getTicketId()));
    }

    // TODO: 고민 해보고 추후 수정이 필요한 부분
    private String generateSerialNumber() {
        String hash = UUID.randomUUID().toString().substring(0, 8);
        return "#" + hash;
    }
}
