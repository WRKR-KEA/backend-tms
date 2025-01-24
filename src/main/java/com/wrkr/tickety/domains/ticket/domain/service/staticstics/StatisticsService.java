package com.wrkr.tickety.domains.ticket.domain.service.staticstics;

import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketPersistenceAdapter;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final TicketPersistenceAdapter ticketPersistenceAdapter;

    public Long getStatisticsByCategoryAndDateRange(Long categoryId, LocalDateTime startOfMonth, LocalDateTime startOfNextMonth) {

        return ticketPersistenceAdapter.getTicketCountByCategoryAndDateRange(categoryId, startOfMonth, startOfNextMonth);
    }

}
