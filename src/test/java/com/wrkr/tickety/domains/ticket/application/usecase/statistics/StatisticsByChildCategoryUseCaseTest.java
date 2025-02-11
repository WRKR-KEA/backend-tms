package com.wrkr.tickety.domains.ticket.application.usecase.statistics;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByCategoryResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByCategoryResponse.StatisticData;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.TicketCount;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.statistics.StatisticsGetService;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.date.DateUtil;
import com.wrkr.tickety.global.utils.date.TimePeriod;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
class StatisticsByChildCategoryUseCaseTest {

    @InjectMocks
    StatisticsByChildCategoryUseCase statisticsByChildCategoryUseCase;

    @Mock
    CategoryGetService categoryGetService;

    @Mock
    StatisticsGetService statisticsGetService;

    private static List<Category> childCategories;
    private static List<Long> ticketCountByCategoryList;

    @BeforeAll
    static void setUp() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @BeforeAll
    static void setupChildCategories() {
        childCategories = IntStream.range(0, 4).mapToObj(i -> Category.builder().categoryId((long) i).seq(i).name("category" + i)
                .parent(Category.builder().categoryId(10L).parent(null).seq(1).name("parent").isDeleted(false).build()).isDeleted(false).build())
            .collect(Collectors.toList());
        ticketCountByCategoryList = List.of(1L, 2L, 3L, 4L);
    }

    @Test
    @DisplayName("2차 카테고리별 일간 통계를 조회한다.")
    void getStatisticsByCategoryInDaily() {
        // given
        StatisticsType statisticsType = StatisticsType.DAILY;
        LocalDate requestDate = LocalDate.now();
        LocalDateTime date = requestDate.atStartOfDay();
        TimePeriod timePeriod = DateUtil.extractTimePeriod(date, statisticsType);
        Long parentCategoryId = 10L;

        StatisticData statisticData = StatisticData.builder().firstCategoryTicketCount(TicketCount.from(ticketCountByCategoryList, childCategories)).build();

        StatisticsByCategoryResponse expect = StatisticsByCategoryResponse.builder().parentCategoryId(PkCrypto.encrypt(parentCategoryId))
            .date(date.format(DateTimeFormatter.ISO_DATE)).statisticData(statisticData).build();

        //when
        when(categoryGetService.getChildren(parentCategoryId)).thenReturn(childCategories);
        when(statisticsGetService.getTicketCountByCategoryList(eq(childCategories), any(TimePeriod.class))).thenReturn(ticketCountByCategoryList);

        //then
        StatisticsByCategoryResponse statisticsByCategoryResponse = statisticsByChildCategoryUseCase.getStatisticsByCategory(statisticsType, requestDate,
                                                                                                                             parentCategoryId);
        assertNotNull(statisticsByCategoryResponse, "2차 카테고리별 일간 통계 조회 결과는 null이 아니어야 합니다.");
        assertNotNull(statisticsByCategoryResponse.parentCategoryId(), "2차 카테고리별 일간 통계 조회 결과의 부모 카테고리 id는 null이 아니어야 합니다.");
        assertEquals(statisticsByCategoryResponse.parentCategoryId(), expect.parentCategoryId(), "2차 카테고리별 일간 통계 조회 결과의 부모 카테고리 id는 암호화 되어야 합니다.");
        assertEquals(statisticsByCategoryResponse.date(), expect.date(), "2차 카테고리별 일간 통계 조회 결과의 날짜는 yyyy-MM-dd 형식이어야 합니다.");
        for (int i = 0; i < expect.statisticData().firstCategoryTicketCount().size(); i++) {
            TicketCount actual = statisticsByCategoryResponse.statisticData().firstCategoryTicketCount().get(i);
            TicketCount expected = expect.statisticData().firstCategoryTicketCount().get(i);
            assertEquals(actual.categoryId(), expected.categoryId());

        }
    }

    @Test
    @DisplayName("2차 카테고리별 월간 통계를 조회한다.")
    void getStatisticsByCategoryInMonthly() {
        // given
        StatisticsType statisticsType = StatisticsType.MONTHLY;
        LocalDate requestDate = LocalDate.now();
        LocalDateTime date = requestDate.atStartOfDay();
        TimePeriod timePeriod = DateUtil.extractTimePeriod(date, statisticsType);
        Long parentCategoryId = 10L;

        StatisticData statisticData = StatisticData.builder().firstCategoryTicketCount(TicketCount.from(ticketCountByCategoryList, childCategories)).build();

        StatisticsByCategoryResponse expect = StatisticsByCategoryResponse.builder().parentCategoryId(PkCrypto.encrypt(parentCategoryId))
            .date(date.format(DateTimeFormatter.ISO_DATE)).statisticData(statisticData).build();

        //when
        when(categoryGetService.getChildren(parentCategoryId)).thenReturn(childCategories);
        when(statisticsGetService.getTicketCountByCategoryList(eq(childCategories), any(TimePeriod.class))).thenReturn(ticketCountByCategoryList);

        //then
        StatisticsByCategoryResponse statisticsByCategoryResponse = statisticsByChildCategoryUseCase.getStatisticsByCategory(statisticsType, requestDate,
                                                                                                                             parentCategoryId);
        assertNotNull(statisticsByCategoryResponse, "2차 카테고리별 일간 통계 조회 결과는 null이 아니어야 합니다.");
        assertNotNull(statisticsByCategoryResponse.parentCategoryId(), "2차 카테고리별 일간 통계 조회 결과의 부모 카테고리 id는 null이 아니어야 합니다.");
        assertEquals(statisticsByCategoryResponse.parentCategoryId(), expect.parentCategoryId(), "2차 카테고리별 일간 통계 조회 결과의 부모 카테고리 id는 암호화 되어야 합니다.");
        assertEquals(statisticsByCategoryResponse.date(), expect.date(), "2차 카테고리별 일간 통계 조회 결과의 날짜는 yyyy-MM-dd 형식이어야 합니다.");
        for (int i = 0; i < expect.statisticData().firstCategoryTicketCount().size(); i++) {
            TicketCount actual = statisticsByCategoryResponse.statisticData().firstCategoryTicketCount().get(i);
            TicketCount expected = expect.statisticData().firstCategoryTicketCount().get(i);
            assertEquals(actual.categoryId(), expected.categoryId());

        }
    }
}