package com.wrkr.tickety.domains.ticket.domain.service.staticstics;

import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketPersistenceAdapter;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final TicketPersistenceAdapter ticketPersistenceAdapter;

    public Long getDailyStatisticsByCategory(Long categoryId, LocalDate date) {

        return ticketPersistenceAdapter.getDailyTicketCountByCategory(categoryId, date);
    }

    public Long getStatisticsByCategoryAndDateRange(Long categoryId, LocalDate startOfMonth, LocalDate startOfNextMonth) {

        return ticketPersistenceAdapter.getMonthlyTicketCountByCategory(categoryId, startOfMonth, startOfNextMonth);
    }

}
