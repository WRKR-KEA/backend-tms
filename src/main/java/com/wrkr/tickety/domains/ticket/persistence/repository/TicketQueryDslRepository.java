package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.domain.constant.SortType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface TicketQueryDslRepository {

    Page<TicketEntity> findByManagerFilters(
        @Param("managerId") Long managerId,
        @Param("status") TicketStatus status,
        Pageable pageable,
        @Param("search") String search,
        @Param("sort") SortType sortType
    );
}
