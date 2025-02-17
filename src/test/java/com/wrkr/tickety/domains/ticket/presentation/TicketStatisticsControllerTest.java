package com.wrkr.tickety.domains.ticket.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(controllers = TicketStatisticsController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
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

    @MockitoBean
    private StatisticsByChildCategoryUseCase statisticsByChildCategoryUseCase;

    @MockitoBean
    private JwtUtils jwtUtils;

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
            mockMvc.perform(
                post("/api/manager/statistics/{statisticsType}", StatisticsType.DAILY.toString()).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk()).andReturn();
        }

        @Test
        @DisplayName("부모카테고리별 월별 통계 조회")
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
            mockMvc.perform(
                post("/api/manager/statistics/{statisticsType}", StatisticsType.MONTHLY.toString()).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk()).andReturn();
        }

        @Test
        @DisplayName("지원되지 않는 통계 타입은 예외 발생")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw")
        void throwExceptionWhenUnsupportedStatisticsType() throws Exception {
            final String UN_SUPPORTED_STATISTICS_TYPE = "unsupportedStatisticsType";
            //given
            StatisticsByCategoryRequest request = StatisticsByCategoryRequest.builder().date(LocalDate.now()).build();
            //then
            mockMvc.perform(
                    post("/api/manager/statistics/{statisticsType}", UN_SUPPORTED_STATISTICS_TYPE)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("날짜 형식이 잘못된 경우 예외 발생")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw")
        void throwExceptionWhenInvalidDateFormat() throws Exception {
            // given - when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                .post("/api/manager/statistics/{statisticsType}", StatisticsType.DAILY.toString())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("{date:invalidDate}"));

            // then
            mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andDo(
                    document(
                        "Statistics/Request/Failure/Case1",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("statisticsType").description("통계 타입 (DAILY | MONTHLY | YEARLY | TOTAL)")
                        ),
                        responseFields(
                            fieldWithPath("isSuccess").description("false"),
                            fieldWithPath("code").description("COMMON_002"),
                            fieldWithPath("message").description("올바르지 않은 요청 형식입니다.")
                        )
                    ))
            ;
        }
    }
}
