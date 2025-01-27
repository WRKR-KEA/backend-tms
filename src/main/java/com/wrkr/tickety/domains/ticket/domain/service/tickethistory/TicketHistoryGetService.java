package com.wrkr.tickety.domains.ticket.domain.service.tickethistory;

import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse.TicketCount;
import com.wrkr.tickety.domains.ticket.domain.constant.ModifiedType;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.TicketHistory;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketHistoryPersistenceAdapter;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketHistoryGetService {

    private final TicketHistoryPersistenceAdapter ticketHistoryPersistenceAdapter;

    public LocalDateTime getFirstManagerChangeDate(Long ticketId) {
        return ticketHistoryPersistenceAdapter.findFirstByTicketIdAndModifiedOrderByCreatedAtAsc(
            ticketId,
            ModifiedType.MANAGER
        ).map(TicketHistory::getCreatedAt).orElse(null);
    }

    public List<TicketCount> getTicketCountStatistics(
        LocalDateTime startDate,
        LocalDateTime endDate,
        StatisticsType statisticsType,
        TicketStatus ticketStatus
    ) {
        return ticketHistoryPersistenceAdapter.countByTicketStatusAndPeriod(
            startDate,
            endDate,
            statisticsType,
            ticketStatus
        );
    }
}
