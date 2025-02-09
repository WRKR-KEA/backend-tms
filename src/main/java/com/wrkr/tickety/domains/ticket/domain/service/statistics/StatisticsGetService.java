package com.wrkr.tickety.domains.ticket.domain.service.statistics;

import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketPersistenceAdapter;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsGetService {

    private final TicketPersistenceAdapter ticketPersistenceAdapter;

    public Long getStatisticsByCategoryAndDateRange(Long categoryId, LocalDateTime startOfMonth, LocalDateTime startOfNextMonth) {
        return ticketPersistenceAdapter.findTicketCountByCategoryAndDateRange(categoryId, startOfMonth, startOfNextMonth);
    }

}
