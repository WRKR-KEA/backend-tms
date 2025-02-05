package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import java.time.LocalDate;
import org.springframework.data.domain.Page;

public interface TicketQueryDslRepository {

    Page<TicketEntity> getAll(String query, TicketStatus status, LocalDate startDate, LocalDate endDate, ApplicationPageRequest pageRequest);

    Page<TicketEntity> findByManagerFilters(Long managerId, TicketStatus status, ApplicationPageRequest pageable, String query);
}
