package com.wrkr.tickety.domains.ticket.domain.service.statistics;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.persistence.adapter.CategoryPersistenceAdapter;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketPersistenceAdapter;
import com.wrkr.tickety.global.utils.date.TimePeriod;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsGetService {

    private final TicketPersistenceAdapter ticketPersistenceAdapter;
    private final CategoryPersistenceAdapter categoryPersistenceAdapter;

    public List<Long> getTicketCountByCategoryList(List<Category> categoryList, TimePeriod timePeriod) {
        return categoryList.stream().map(
            category -> ticketPersistenceAdapter.findTicketCountByCategoryAndDateRange(category.getCategoryId(), timePeriod.getStartDateTime(),
                                                                                       timePeriod.getEndDateTime())).toList();
    }

    public List<Long> getTicketCountByParentCategoryList(List<Category> categoryList, TimePeriod timePeriod) {
        List<Long> ticketCountList = new ArrayList<>();
        IntStream.range(0, categoryList.size()).forEach(i -> {
            Category category = categoryList.get(i);
            List<Category> children = categoryPersistenceAdapter.findChildren(category.getCategoryId());
            long count = children.stream().mapToLong(
                child -> ticketPersistenceAdapter.findTicketCountByCategoryAndDateRange(child.getCategoryId(), timePeriod.getStartDateTime(),
                                                                                        timePeriod.getEndDateTime())).sum();
            ticketCountList.add(count);
        });
        return ticketCountList;
    }

}
