package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse.TicketCount;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface TicketHistoryQueryDslRepository {

    List<TicketCount> countByTicketStatusDuringPeriod(LocalDateTime startDate, LocalDateTime endDate, StatisticsType statisticsType,
        TicketStatus ticketStatus);
}
