package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketExcelPreResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;

public interface TicketQueryDslRepository {

    Page<TicketEntity> getAll(String query, TicketStatus status, LocalDate startDate, LocalDate endDate, ApplicationPageRequest pageRequest);

    List<DepartmentTicketExcelPreResponse> getAllTicketsNoPaging(String query, TicketStatus status, LocalDate startDate, LocalDate endDate);

    Page<TicketEntity> findByManagerFilters(Long managerId, TicketStatus status, ApplicationPageRequest pageable, String query, List<Long> categoryIdList);
}
