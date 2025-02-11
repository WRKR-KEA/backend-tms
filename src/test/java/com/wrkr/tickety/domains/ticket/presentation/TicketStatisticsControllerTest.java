package com.wrkr.tickety.domains.ticket.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.ticket.application.dto.request.StatisticsByCategoryRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByCategoryResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByCategoryResponse.StatisticData;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.TicketCount;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsByChildCategoryUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsByParentCategoryUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsByStatusUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsGetUseCase;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = TicketStatisticsController.class)
@AutoConfigureMockMvc
class TicketStatisticsControllerTest {


    private static final Logger log = LoggerFactory.getLogger(TicketStatisticsControllerTest.class);
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

    @MockitoBean
    private StatisticsByChildCategoryUseCase statisticsByChildCategoryUseCase;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Nested
    class statisticsByParentCategoryTest {


        @Test
        @DisplayName("부모 카테고리별 통계 조회")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw")
        void getParentCategoryStatistics() throws Exception {
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
            when(statisticsByParentCategoryUseCase.getStatisticsByCategory(StatisticsType.MONTHLY, requestDate)).thenReturn(expectedResponse);

            //then
            MvcResult result = mockMvc.perform(
                    post(
                        "/api/manager/statistics/{statisticsType}", "DAILY")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andReturn();
        }

    }
}