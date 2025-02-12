package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper.mapToTicket;
import static com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper.toTicketPkResponse;
import static com.wrkr.tickety.global.utils.PkCrypto.decrypt;
import static com.wrkr.tickety.global.utils.PkCrypto.encrypt;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketHistoryMapper;
import com.wrkr.tickety.domains.ticket.domain.constant.ModifiedType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.model.TicketHistory;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketSaveService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistorySaveService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private final TicketGetService ticketGetService;

    public TicketPkResponse createTicket(TicketCreateRequest request, Long userId) {
        Category childCategory = categoryGetService.getChildrenCategory(decrypt(request.categoryId()));
        Member member = UserGetService.byMemberId(userId);

        String serialNumber = generateSerialNumber(childCategory);
        TicketStatus status = TicketStatus.REQUEST;

        Ticket ticket = mapToTicket(request, childCategory, serialNumber, status, member);
        Ticket savedTicket = ticketSaveService.save(ticket);

        ModifiedType modifiedType = ModifiedType.STATUS;
        TicketHistory ticketHistory = TicketHistoryMapper.mapToTicketHistory(savedTicket,
            modifiedType);
        ticketHistorySaveService.save(ticketHistory);

        return toTicketPkResponse(encrypt(savedTicket.getTicketId()));
    }

    private String generateSerialNumber(Category childCategory) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String prefix = today + childCategory.getParent().getAbbreviation() + childCategory.getAbbreviation();
        String sequence = ticketGetService.findLastSequence(today, childCategory) == null ? "01" :
            String.format("%02d", Integer.parseInt(ticketGetService.findLastSequence(today, childCategory)) + 1);

        return "#" + prefix + sequence;
    }
}
