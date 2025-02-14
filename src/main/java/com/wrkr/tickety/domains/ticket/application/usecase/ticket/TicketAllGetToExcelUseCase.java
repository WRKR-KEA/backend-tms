package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketExcelPreResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketExcelResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketAllGetToExcelUseCase {

    private final TicketGetService ticketGetService;

    public List<DepartmentTicketExcelResponse> getAllTicketsNoPaging(String queryReq, String statusReq, String startDateReq, String endDateReq) {
        LocalDate startDate = parseLocalDateOrNull(startDateReq);
        LocalDate endDate = parseLocalDateOrNull(endDateReq);

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw ApplicationException.from(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID);
        }

        String query = queryReq == null || queryReq.isBlank() ? null : queryReq;
        TicketStatus status = TicketStatus.from(statusReq);

        List<DepartmentTicketExcelPreResponse> departmentAllTicketsNoPaging = ticketGetService.getDepartmentAllTicketsNoPaging(query, status, startDate,
            endDate);

        return departmentAllTicketsNoPaging.stream()
            .map(TicketMapper::mapToDepartmentTicketExcelResponse)
            .toList();
    }

    private LocalDate parseLocalDateOrNull(String date) {
        try {
            return LocalDate.parse(date);
        } catch (NullPointerException | DateTimeException e) {
            return null;
        }
    }
}
