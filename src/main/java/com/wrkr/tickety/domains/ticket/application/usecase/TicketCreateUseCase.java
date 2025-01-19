package com.wrkr.tickety.domains.ticket.application.usecase;

import com.wrkr.tickety.domains.ticket.application.dto.request.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.TicketSaveService;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

import static com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper.mapToTicket;

@UseCase
@RequiredArgsConstructor
@Transactional
public class TicketCreateUseCase {

    private final TicketSaveService ticketSaveService;
    private final CategoryGetService categoryGetService;

    public String createTicket(TicketCreateRequest request) {
        // TODO: 사용자랑 담당자 정보만 가져오면 됌. 그리고 담당자 정보는 nullable하는게 어떤지..?
        Category category = categoryGetService.getCategory(request.categoryId())
            .orElseThrow(() -> new ApplicationException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        String serialNumber = generateSerialNumber(category);
        TicketStatus status = TicketStatus.REQUEST;

        Ticket ticket = mapToTicket(request, category, serialNumber, status);
        Ticket savedTicket = ticketSaveService.save(ticket);

        return PkCrypto.encrypt(savedTicket.getTicketId());
    }

    private String generateSerialNumber(Category category) {
        String categoryCode = category.getCode();
        String hash = UUID.randomUUID().toString().substring(0, 8); // 8자리 해시 값

        return categoryCode + "-" + hash;
    }
}
