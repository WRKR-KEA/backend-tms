package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import com.wrkr.tickety.global.common.dto.PageRequest;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketQueryDslRepository {

    Page<TicketEntity> getAll(String query, TicketStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<TicketEntity> findByManagerFilters(Long managerId, TicketStatus status, PageRequest pageable, String query);
}
