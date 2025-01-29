package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.DepartmentTicketRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PageResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
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
public class DepartmentTicketUseCase {

    private final TicketGetService ticketGetService;

    public PageResponse<DepartmentTicketResponse> getDepartmentTicket(DepartmentTicketRequest request, Pageable pageable) {

        LocalDate startDate = parseLocalDateOrNull(request.startDate());
        LocalDate endDate = parseLocalDateOrNull(request.endDate());
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw ApplicationException.from(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }

        String query = request.query() == null || request.query().isBlank() ? null : request.query();
        TicketStatus status = TicketStatus.fromValueOrNull(request.status());

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
