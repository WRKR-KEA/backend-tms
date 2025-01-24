package com.wrkr.tickety.domains.ticket.application.usecase.statistics;

import com.wrkr.tickety.domains.ticket.application.dto.response.StatisticsByCategoryResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.StatisticsByCategoryResponse.StatisticData;
import com.wrkr.tickety.domains.ticket.application.dto.response.StatisticsByCategoryResponse.TicketCount;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.staticstics.StatisticsService;
import com.wrkr.tickety.domains.ticket.exception.DateErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.time.LocalDate;
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

    private final StatisticsService statisticsService;
    private final CategoryGetService categoryGetService;
    private final PkCrypto pkCrypto;

    public StatisticsByCategoryResponse getStatisticsByCategory(StatisticsType type, String date) {

        LocalDate requestDate;
        try {
            requestDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            throw ApplicationException.from(DateErrorCode.INVALID_DATE_FORMAT);
        }
        List<Category> firstCategoryList = new ArrayList<>();

        List<Category> secondCategoryList = new ArrayList<>();

        categoryGetService.byIsDeleted().forEach(category -> {
            if (category.getParent() == null) {
                firstCategoryList.add(category);
            } else {
                secondCategoryList.add(category);
            }
        });

        List<TicketCount> firstCategoryTicketCountList = mappingTicketCount(firstCategoryList, type, requestDate);

        List<TicketCount> secondCategoryTicketCountList = mappingTicketCount(secondCategoryList, type, requestDate);

        StatisticData statisticData = StatisticData.builder()
            .FirstCategoryTicketCount(firstCategoryTicketCountList)
            .SecondCategoryTicketCount(secondCategoryTicketCountList).build();

        return StatisticsByCategoryResponse.builder()
            .date(date)
            .statisticData(statisticData)
            .build();
    }

    private List<TicketCount> mappingTicketCount(List<Category> categoryList, StatisticsType type, LocalDate requestDate) {

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
    private List<Long> getTicketCountByCategoryList(List<Category> categoryList, StatisticsType type, LocalDate requestDate) {

        return categoryList.stream()
            .map(category -> switch (type) {

                case StatisticsType.DAILY:
                    LocalDate startOfNextDay = requestDate.plusDays(1);
                    yield statisticsService.getStatisticsByCategoryAndDateRange(category.getCategoryId(), requestDate, startOfNextDay);

                case StatisticsType.MONTHLY:
                    LocalDate startOfMonth = requestDate.withDayOfMonth(1);
                    LocalDate startOfNextMonth = startOfMonth.plusMonths(1);
                    yield statisticsService.getStatisticsByCategoryAndDateRange(category.getCategoryId(), startOfMonth, startOfNextMonth);

                case StatisticsType.YEARLY:
                    LocalDate startOfYear = requestDate.withDayOfYear(1);
                    LocalDate startOfNextYear = startOfYear.plusYears(1);
                    yield statisticsService.getStatisticsByCategoryAndDateRange(category.getCategoryId(), startOfYear, startOfNextYear);

            })
            .toList();
    }
}
