package com.wrkr.tickety.domains.ticket.application.usecase.statistics;

import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByCategoryResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByCategoryResponse.StatisticData;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.TicketCount;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.statistics.StatisticsGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.utils.date.DateUtil;
import com.wrkr.tickety.global.utils.date.TimePeriod;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class StatisticsByParentCategoryUseCase {

    private final StatisticsGetService statisticsGetService;
    private final CategoryGetService categoryGetService;

    public StatisticsByCategoryResponse getStatisticsByCategory(StatisticsType type, LocalDate requestDate) {
        LocalDateTime date = requestDate.atStartOfDay();
        List<Category> parentCategoryList = categoryGetService.findParents();
        TimePeriod timePeriod = DateUtil.extractTimePeriod(date, type);

        List<Long> ticketCountByCategoryList = statisticsGetService.getTicketCountByParentCategoryList(parentCategoryList, timePeriod);
        List<TicketCount> ticketCountList = TicketCount.from(ticketCountByCategoryList, parentCategoryList);

        StatisticData statisticData = StatisticData.builder()
            .categoryTicketCount(ticketCountList)
            .build();

        return StatisticsByCategoryResponse.builder()
            .date(date.format(DateTimeFormatter.ISO_DATE))
            .statisticData(statisticData).build();
    }
}
