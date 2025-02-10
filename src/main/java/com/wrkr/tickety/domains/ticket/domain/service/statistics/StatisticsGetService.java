package com.wrkr.tickety.domains.ticket.domain.service.statistics;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketPersistenceAdapter;
import com.wrkr.tickety.global.utils.date.TimePeriod;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsGetService {

    private final TicketPersistenceAdapter ticketPersistenceAdapter;

    public List<Long> getTicketCountByCategoryList(List<Category> categoryList, TimePeriod timePeriod) {
        return categoryList.stream().map(category ->
                                             ticketPersistenceAdapter.findTicketCountByCategoryAndDateRange(category.getCategoryId(),
                                                                                                            timePeriod.getStartDateTime(),
                                                                                                            timePeriod.getEndDateTime())
        ).toList();
    }

}
