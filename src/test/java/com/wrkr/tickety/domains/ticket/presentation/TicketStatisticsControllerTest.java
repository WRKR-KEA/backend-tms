package com.wrkr.tickety.domains.ticket.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.ticket.application.dto.request.StatisticsByCategoryRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByCategoryResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByCategoryResponse.StatisticData;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByStatusResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.TicketCount;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsByChildCategoryUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsByParentCategoryUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsByStatusUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsGetUseCase;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TicketStatisticsController.class)
@AutoConfigureMockMvc
class TicketStatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StatisticsByParentCategoryUseCase statisticsByParentCategoryUseCase;

    @MockitoBean
    private StatisticsByStatusUseCase statisticsByStatusUseCase;

    @MockitoBean
    private StatisticsGetUseCase statisticsGetUseCase;

    @InjectMocks
    static PkCrypto pkCrypto;

    @MockitoBean
    private StatisticsByChildCategoryUseCase statisticsByChildCategoryUseCase;

    @MockitoBean
    private JwtUtils jwtUtils;

    @BeforeAll
    static void init() {
        pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Nested
    class statisticsByStatusTest {

        @Test
        @DisplayName("상태별 통계를 조회한다.")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw")
        void getStatusStatisticsInDaily() throws Exception {
            //given
            LocalDate requestDate = LocalDate.now();
            String statisticsType = StatisticsType.DAILY.toString();
            String date = requestDate.toString();
            StatisticsByStatusResponse expectedResponse = StatisticsByStatusResponse.builder().date(requestDate.toString()).accept(10L).reject(5L).request(7L)
                .complete(3L).build();
            //when
            when(statisticsByStatusUseCase.getStatisticsByStatus(StatisticsType.from(statisticsType), date)).thenReturn(expectedResponse);

            //then
            mockMvc.perform(get("/api/manager/statistics/{statisticsType}/status", StatisticsType.DAILY.toString()).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON).queryParam("date", date)).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.date").value(requestDate.toString())).andExpect(jsonPath("$.result.accept").value(expectedResponse.accept()))
                .andExpect(jsonPath("$.result.reject").value(expectedResponse.reject()))
                .andExpect(jsonPath("$.result.request").value(expectedResponse.request()))
                .andExpect(jsonPath("$.result.complete").value(expectedResponse.complete()));
        }

        @Test
        @DisplayName("지원되지 않는 통계 타입은 예외 발생")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw")
        void throwExceptionWhenUnsupportedStatisticsType() throws Exception {
            final String UN_SUPPORTED_STATISTICS_TYPE = "unsupportedStatisticsType";
            //given
            StatisticsByCategoryRequest request = StatisticsByCategoryRequest.builder().date(LocalDate.now()).build();
            //then
            mockMvc.perform(get("/api/manager/statistics/{statisticsType}/status", UN_SUPPORTED_STATISTICS_TYPE).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
        }
    }

    @Nested
    class statisticsByParentCategoryTest {


        @Test
        @DisplayName("부모 카테고리별 통계 조회")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "managr.hjw")
        void getParentCategoryStatisticsInDaily() throws Exception {
            //given

            LocalDate requestDate = LocalDate.now();
            StatisticsByCategoryRequest request = StatisticsByCategoryRequest.builder().date(LocalDate.now()).build();

            List<TicketCount> CategoryTicketCount = new ArrayList<>();
            IntStream.range(0, 5).forEach(i -> {
                CategoryTicketCount.add(TicketCount.builder().categoryId("categoryId" + i).count(i).categoryName("categoryName" + i).build());
            });

            StatisticsByCategoryResponse expectedResponse = StatisticsByCategoryResponse.builder().parentCategoryId(null)
                .statisticData(StatisticData.builder().firstCategoryTicketCount(CategoryTicketCount).build()).date(requestDate.toString()).build();
            //when
            when(statisticsByParentCategoryUseCase.getStatisticsByCategory(StatisticsType.DAILY, requestDate)).thenReturn(expectedResponse);

            //then
            mockMvc.perform(post("/api/manager/statistics/{statisticsType}", StatisticsType.DAILY.toString()).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andReturn();
        }

        @Test
        @DisplayName("부모카테고리별 일별 통계 조회")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "managr.hjw")
        void getParentCategoryStatisticsInMonthly() throws Exception {
            //given

            LocalDate requestDate = LocalDate.now();
            StatisticsByCategoryRequest request = StatisticsByCategoryRequest.builder().date(LocalDate.now()).build();

            List<TicketCount> CategoryTicketCount = new ArrayList<>();
            IntStream.range(0, 5)
                .forEach(i -> CategoryTicketCount.add(TicketCount.builder().categoryId("categoryId" + i).count(i).categoryName("categoryName" + i).build()));

            StatisticsByCategoryResponse expectedResponse = StatisticsByCategoryResponse.builder().parentCategoryId(null)
                .statisticData(StatisticData.builder().firstCategoryTicketCount(CategoryTicketCount).build()).date(requestDate.toString()).build();
            //when
            when(statisticsByParentCategoryUseCase.getStatisticsByCategory(StatisticsType.MONTHLY, requestDate)).thenReturn(expectedResponse);

            //then
            mockMvc.perform(post("/api/manager/statistics/{statisticsType}", StatisticsType.MONTHLY.toString()).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andReturn();
        }

        @Test
        @DisplayName("지원되지 않는 통계 타입은 예외 발생")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw")
        void throwExceptionWhenUnsupportedStatisticsType() throws Exception {
            final String UN_SUPPORTED_STATISTICS_TYPE = "unsupportedStatisticsType";
            //given
            StatisticsByCategoryRequest request = StatisticsByCategoryRequest.builder().date(LocalDate.now()).build();
            //then
            mockMvc.perform(post("/api/manager/statistics/{statisticsType}", UN_SUPPORTED_STATISTICS_TYPE).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("날짜 형식이 잘못된 경우 예외 발생")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw")
        void throwExceptionWhenInvalidDateFormat() throws Exception {
            //then
            mockMvc.perform(post("/api/manager/statistics/{statisticsType}", StatisticsType.DAILY.toString()).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString("{date:invalidDate}")))
                .andExpect(status().isBadRequest());
        }
    }
}