package com.wrkr.tickety.domains.ticket.application.usecase.statistics;

import com.wrkr.tickety.domains.ticket.application.dto.response.StatisticsByCategoryResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.StatisticsByCategoryResponse.StatisticData;
import com.wrkr.tickety.domains.ticket.application.dto.response.StatisticsByCategoryResponse.TicketCount;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.staticstics.StatisticsGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.date.DateUtil;
import com.wrkr.tickety.global.utils.date.TimePeriod;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class StatisticsByCategoryUseCase {

    private final StatisticsGetService statisticsGetService;
    private final CategoryGetService categoryGetService;
    private final PkCrypto pkCrypto;

    public StatisticsByCategoryResponse getStatisticsByCategory(StatisticsType type, LocalDate requestDate) {
        LocalDateTime date = requestDate.atStartOfDay();
        List<Category> firstCategoryList = new ArrayList<>();

        List<Category> secondCategoryList = new ArrayList<>();

        categoryGetService.byIsDeleted().forEach(category -> {
            if (category.getParent() == null) {
                firstCategoryList.add(category);
            } else {
                secondCategoryList.add(category);
            }
        });

        List<TicketCount> firstCategoryTicketCountList = mappingTicketCount(firstCategoryList, type, date);

        List<TicketCount> secondCategoryTicketCountList = mappingTicketCount(secondCategoryList, type, date);

        StatisticData statisticData = StatisticData.builder()
            .firstCategoryTicketCount(firstCategoryTicketCountList)
            .secondCategoryTicketCount(secondCategoryTicketCountList).build();

        return StatisticsByCategoryResponse.builder()
            .date(date.format(DateTimeFormatter.ISO_DATE))
            .statisticData(statisticData)
            .build();
    }

    private List<TicketCount> mappingTicketCount(List<Category> categoryList, StatisticsType type, LocalDateTime requestDate) {
        List<Long> ticketCountByCategory = getTicketCountByCategoryList(categoryList, type, requestDate);

        return IntStream.range(0, categoryList.size())
            .mapToObj(index -> TicketCount.builder()
                .count(ticketCountByCategory.get(index))
                .categoryId(pkCrypto.encryptValue(categoryList.get(index).getCategoryId()))
                .categoryName(categoryList.get(index).getName()).build())
            .toList();
    }


    /**
     * type에 따라 일별, 월별, 연별 티켓 수를 가져온다.
     */
    private List<Long> getTicketCountByCategoryList(List<Category> categoryList, StatisticsType type, LocalDateTime requestDateTime) {
        return categoryList.stream()
            .map(category -> switch (type) {

                case TOTAL -> {
                    TimePeriod timePeriod = DateUtil.extractTimePeriod(requestDateTime, type);
                    yield statisticsGetService.getStatisticsByCategoryAndDateRange(category.getCategoryId(), timePeriod.getStartDateTime(),
                        timePeriod.getEndDateTime());
                }

                case DAILY -> {
                    LocalDateTime startOfDay = requestDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
                    LocalDateTime startOfNextDay = startOfDay.plusDays(1);
                    yield statisticsGetService.getStatisticsByCategoryAndDateRange(category.getCategoryId(), startOfDay, startOfNextDay);
                }

                case MONTHLY -> {
                    LocalDateTime startOfMonth = requestDateTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                    LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);
                    yield statisticsGetService.getStatisticsByCategoryAndDateRange(category.getCategoryId(), startOfMonth, startOfNextMonth);
                }

                case YEARLY -> {
                    LocalDateTime startOfYear = requestDateTime.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                    LocalDateTime startOfNextYear = startOfYear.plusYears(1);
                    yield statisticsGetService.getStatisticsByCategoryAndDateRange(category.getCategoryId(), startOfYear, startOfNextYear);
                }

            })
            .toList();
    }

}
