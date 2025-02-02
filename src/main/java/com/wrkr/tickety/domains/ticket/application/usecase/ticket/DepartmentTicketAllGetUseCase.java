package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.common.dto.PageResponse;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import java.time.DateTimeException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DepartmentTicketAllGetUseCase {

    private final TicketGetService ticketGetService;

    public PageResponse<DepartmentTicketResponse> getDepartmentTicketList(String queryReq, String statusReq, String startDateReq, String endDateReq,
        Pageable pageable) {

        LocalDate startDate = parseLocalDateOrNull(startDateReq);
        LocalDate endDate = parseLocalDateOrNull(endDateReq);
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw ApplicationException.from(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }

        String query = queryReq == null || queryReq.isBlank() ? null : queryReq;
        TicketStatus status = TicketStatus.from(statusReq);

        return TicketMapper.toDepartmentTicketResponse(ticketGetService.getDepartmentTickets(query, status, startDate, endDate, pageable));
    }

    private LocalDate parseLocalDateOrNull(String date) {
        try {
            return LocalDate.parse(date);
        } catch (NullPointerException | DateTimeException e) {
            return null;
        }
    }
}
