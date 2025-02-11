package com.wrkr.tickety.domains.ticket.application.usecase.statistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByCategoryResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.TicketCount;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.statistics.StatisticsGetService;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.date.TimePeriod;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_METHOD)
class StatisticsByParentCategoryUseCaseTest {

    @InjectMocks
    private StatisticsByParentCategoryUseCase statisticsByParentCategoryUseCase;

    @Mock
    private StatisticsGetService statisticsGetService;

    @InjectMocks
    static PkCrypto pkCrypto;

    @Mock
    private CategoryGetService categoryGetService;

    private static List<Category> parentCategoryList;
    private static List<Long> ticketCountByCategoryList;

    @BeforeAll
    static void setUp() {
        pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();

        parentCategoryList = IntStream.range(1, 10)
            .mapToObj(i -> Category.builder().categoryId((long) i).parent(null).seq(i).name("parent" + i).isDeleted(false).build())
            .collect(Collectors.toList());

        ticketCountByCategoryList = IntStream.range(1, 10).mapToObj(i -> (long) i).collect(Collectors.toList());
    }

    @Test
    @DisplayName("일간 카테고리별 티켓 수 통계를 조회한다.")
    void getStatisticsByCategoryInDaily() {
        // given
        StatisticsType type = StatisticsType.DAILY;
        LocalDate requestDate = LocalDate.now();
        String expectedDate = requestDate.atStartOfDay().format(DateTimeFormatter.ISO_DATE);

        when(categoryGetService.findParents()).thenReturn(parentCategoryList);
        when(statisticsGetService.getTicketCountByParentCategoryList(eq(parentCategoryList), any(TimePeriod.class))).thenReturn(ticketCountByCategoryList);

        // when
        StatisticsByCategoryResponse response = statisticsByParentCategoryUseCase.getStatisticsByCategory(type, requestDate);

        // then
        assertNotNull(response, "응답 객체는 null이 아니어야 합니다.");
        assertEquals(expectedDate, response.date(), "날짜는 ISO_DATE 형식이어야 합니다.");
        assertNull(response.parentCategoryId(), "부모 카테고리 ID는 null이어야 합니다.");

        List<TicketCount> ticketCounts = response.statisticData().firstCategoryTicketCount();
        assertNotNull(ticketCounts, "티켓 수 리스트는 null이 아니어야 합니다.");
        assertEquals(parentCategoryList.size(), ticketCounts.size(), "부모 카테고리 개수와 티켓 수 리스트의 크기는 같아야 합니다.");

        for (int i = 0; i < parentCategoryList.size(); i++) {
            Category category = parentCategoryList.get(i);
            TicketCount ticketCount = ticketCounts.get(i);
            assertEquals(category.getName(), ticketCount.categoryName(), "인덱스 " + i + "의 카테고리명이 일치해야 합니다.");
            assertEquals(ticketCountByCategoryList.get(i), ticketCount.count(), "인덱스 " + i + "의 티켓 수가 일치해야 합니다.");
        }

        // verify
        verify(categoryGetService).findParents();
        verify(statisticsGetService).getTicketCountByParentCategoryList(eq(parentCategoryList), any(TimePeriod.class));
    }

    @Test
    @DisplayName("월간 카테고리별 티켓 수 통계를 조회한다.")
    void getStatisticsByCategoryInMonthly() {
        // given
        StatisticsType type = StatisticsType.MONTHLY;
        LocalDate requestDate = LocalDate.now();
        String expectedDate = requestDate.atStartOfDay().format(DateTimeFormatter.ISO_DATE);

        when(categoryGetService.findParents()).thenReturn(parentCategoryList);
        when(statisticsGetService.getTicketCountByParentCategoryList(eq(parentCategoryList), any(TimePeriod.class))).thenReturn(ticketCountByCategoryList);

        // when
        StatisticsByCategoryResponse response = statisticsByParentCategoryUseCase.getStatisticsByCategory(type, requestDate);

        // then
        assertNotNull(response, "응답 객체는 null이 아니어야 합니다.");
        assertEquals(expectedDate, response.date(), "날짜는 ISO_DATE 형식이어야 합니다.");
        assertNull(response.parentCategoryId(), "부모 카테고리 ID는 null이어야 합니다.");

        List<TicketCount> ticketCounts = response.statisticData().firstCategoryTicketCount();
        assertNotNull(ticketCounts, "티켓 수 리스트는 null이 아니어야 합니다.");
        assertEquals(parentCategoryList.size(), ticketCounts.size(), "부모 카테고리 개수와 티켓 수 리스트의 크기는 같아야 합니다.");

        for (int i = 0; i < parentCategoryList.size(); i++) {
            Category category = parentCategoryList.get(i);
            TicketCount ticketCount = ticketCounts.get(i);
            assertEquals(category.getName(), ticketCount.categoryName(), "인덱스 " + i + "의 카테고리명이 일치해야 합니다.");
            assertEquals(ticketCountByCategoryList.get(i), ticketCount.count(), "인덱스 " + i + "의 티켓 수가 일치해야 합니다.");
        }
        //verify
        verify(categoryGetService).findParents();
        verify(statisticsGetService).getTicketCountByParentCategoryList(eq(parentCategoryList), any(TimePeriod.class));
    }
}
