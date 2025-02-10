package com.wrkr.tickety.domains.ticket.application.usecase.statistics;

import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByCategoryResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByCategoryResponse.StatisticData;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.TicketCount;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.statistics.StatisticsGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.date.DateUtil;
import com.wrkr.tickety.global.utils.date.TimePeriod;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class StatisticsByChildCategoryUseCase {

    private final CategoryGetService categoryGetService;
    private final StatisticsGetService statisticsGetService;

    public StatisticsByCategoryResponse getStatisticsByCategory(StatisticsType statisticsType, LocalDate requestDate, Long parentCategoryId) {
        LocalDateTime date = requestDate.atStartOfDay();
        TimePeriod timePeriod = DateUtil.extractTimePeriod(date, statisticsType);

        List<Category> childCategoryList = categoryGetService.getChildren(parentCategoryId);
        List<Long> ticketCountByCategoryList = statisticsGetService.getTicketCountByCategoryList(childCategoryList, timePeriod);

        List<TicketCount> ticketCountList = TicketCount.from(ticketCountByCategoryList, childCategoryList);

        StatisticData statisticData = StatisticData.builder()
            .firstCategoryTicketCount(ticketCountList)
            .build();

        return StatisticsByCategoryResponse.builder()
            .parentCategoryId(PkCrypto.encrypt(parentCategoryId))
            .date(date.format(DateTimeFormatter.ISO_DATE))
            .statisticData(statisticData).build();
    }
}
