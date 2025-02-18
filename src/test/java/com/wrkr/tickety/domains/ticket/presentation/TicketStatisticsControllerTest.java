package com.wrkr.tickety.domains.ticket.presentation;

import static com.wrkr.tickety.domains.ticket.exception.StatisticsErrorCode.ILLEGAL_STATISTICS_OPTION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
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
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.TicketCount;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsByChildCategoryUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsByParentCategoryUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsByStatusUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsGetUseCase;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.exception.ApplicationException;
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
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(controllers = TicketStatisticsController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class TicketStatisticsControllerTest {

    @InjectMocks
    static PkCrypto pkCrypto;
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
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("date", date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.date").value(requestDate.toString())).andExpect(jsonPath("$.result.accept").value(expectedResponse.accept()))
                .andExpect(jsonPath("$.result.reject").value(expectedResponse.reject()))
                .andExpect(jsonPath("$.result.request").value(expectedResponse.request()))
                .andExpect(jsonPath("$.result.complete").value(expectedResponse.complete()))
                .andDo(document("get-statistics-by-status",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("statisticsType")
                                .description("통계 타입 (DAILY | MONTHLY | YEARLY)")
                        ),
                        queryParameters(
                            parameterWithName("date")
                                .description("통계를 확인하고자 하는 기준일 (Optional)")
                        ),
                        responseFields(
                            fieldWithPath("result")
                                .description("일간/월간/연간 상태별 요약 응답"),
                            fieldWithPath("isSuccess").description("성공 여부"),
                            fieldWithPath("code").description("응답 코드"),
                            fieldWithPath("message").description("응답 메시지"),
                            fieldWithPath("result.date").description("통계 날짜"),
                            fieldWithPath("result.accept").description("승인된 티켓 수"),
                            fieldWithPath("result.reject").description("반려된 티켓 수"),
                            fieldWithPath("result.request").description("요청된 티켓 수"),
                            fieldWithPath("result.complete").description("완료된 티켓 수")
                        )
                    )
                );
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
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class statisticsByParentCategoryTest {


        @Test
        @DisplayName("부모 카테고리별 일별 통계 조회")
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
                .statisticData(StatisticData.builder()
                    .categoryTicketCount(CategoryTicketCount)
                    .build()).date(requestDate.toString())
                .build();
            //when
            when(statisticsByParentCategoryUseCase.getStatisticsByCategory(StatisticsType.DAILY, requestDate)).thenReturn(expectedResponse);

            //then
            mockMvc.perform(
                    post("/api/manager/statistics/{statisticsType}", StatisticsType.DAILY.toString()).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("Statistics/Request/Success/Case1",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("statisticsType").description("통계 타입 (DAILY | MONTHLY | YEARLY | TOTAL)")
                        ),
                        requestFields(
                            fieldWithPath("date").description("통계를 확인하고자 하는 날짜")
                        ),
                        responseFields(
                            fieldWithPath("isSuccess").description("성공 여부"),
                            fieldWithPath("code").description("응답 코드 (성공:SUCCESS, 실패:FAILURE)"),
                            fieldWithPath("message").description("응답 메시지 (성공:성공, 실패:실패)"),
                            fieldWithPath("result.date").description("통계를 확인한 날짜"),
                            fieldWithPath("result.parentCategoryId").description("부모 카테고리 id (null)"),
                            fieldWithPath("result.statisticData.categoryTicketCount").description("1차 카테고리 티켓 수 배열"),
                            fieldWithPath("result.statisticData.categoryTicketCount[].categoryId").description("1차 카테고리 id"),
                            fieldWithPath("result.statisticData.categoryTicketCount[].categoryName").description("1차 카테고리 이름"),
                            fieldWithPath("result.statisticData.categoryTicketCount[].count").description("1차 카테고리 티켓 수")
                        )
                    )
                );
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
                .forEach(i -> CategoryTicketCount.add(TicketCount.builder().categoryId("categoryId" + i).count(i).categoryName("categoryName" + i)
                    .build()));

            StatisticsByCategoryResponse expectedResponse = StatisticsByCategoryResponse.builder().parentCategoryId(null)
                .statisticData(StatisticData.builder()
                    .categoryTicketCount(CategoryTicketCount)
                    .build()).date(requestDate.toString())
                .build();
            //when
            when(statisticsByParentCategoryUseCase.getStatisticsByCategory(StatisticsType.MONTHLY, requestDate)).thenReturn(expectedResponse);

            //then
            mockMvc.perform(
                    post("/api/manager/statistics/{statisticsType}", StatisticsType.MONTHLY.toString()).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("Statistics/Request/Success/Case2",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("statisticsType").description("통계 타입 (DAILY | MONTHLY | YEARLY | TOTAL)")
                        ),
                        requestFields(
                            fieldWithPath("date").description("통계를 확인하고자 하는 날짜")
                        ),
                        responseFields(
                            fieldWithPath("isSuccess").description("성공 여부"),
                            fieldWithPath("code").description("응답 코드 (성공:SUCCESS, 실패:FAILURE)"),
                            fieldWithPath("message").description("응답 메시지 (성공:성공, 실패:실패)"),
                            fieldWithPath("result.date").description("통계를 확인한 날짜"),
                            fieldWithPath("result.parentCategoryId").description("부모 카테고리 id (null)"),
                            fieldWithPath("result.statisticData.categoryTicketCount").description("1차 카테고리 티켓 수 배열"),
                            fieldWithPath("result.statisticData.categoryTicketCount[].categoryId").description("1차 카테고리 id"),
                            fieldWithPath("result.statisticData.categoryTicketCount[].categoryName").description("1차 카테고리 이름"),
                            fieldWithPath("result.statisticData.categoryTicketCount[].count").description("1차 카테고리 티켓 수")
                        )
                    )
                );
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
                .post("/api/manager/statistics/{statisticsType}", StatisticsType.DAILY.toString()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString("{date:invalidDate}"));

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

    @Nested
    @DisplayName("기간별 & 상태별 티켓 개수 조회 API 테스트")
    class GetTicketCountStatisticsTest {

        @Test
        @DisplayName("월간 티켓 상태별 통계 조회에 성공한다.")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.kmh")
        void getMonthlyTicketStatisticsSuccess() throws Exception {
            // given
            String date = "2025-01";
            StatisticsType type = StatisticsType.MONTHLY;
            TicketStatus status = TicketStatus.REQUEST;

            StatisticsByTicketStatusResponse response = new StatisticsByTicketStatusResponse(
                date,
                List.of(new StatisticsByTicketStatusResponse.TicketCount("1", 10L))
            );

            given(statisticsGetUseCase.getTicketCountStatistics(eq(date), eq(type), eq(status)))
                .willReturn(response);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders
                    .get("/api/manager/statistics/count")
                    .param("date", date)
                    .param("type", type.name())
                    .param("status", status.name())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.isSuccess").value(true),
                    jsonPath("$.code").exists(),
                    jsonPath("$.message").exists(),
                    jsonPath("$.result.baseDate").value(date),
                    jsonPath("$.result.countList").isArray(),
                    jsonPath("$.result.countList[*].targetDate").exists(),
                    jsonPath("$.result.countList[*].count").exists()
                )
                .andDo(document(
                    "Statistics/GetTicketStatistics/Success/Monthly",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("result.baseDate").type(JsonFieldType.STRING).description("조회 기준 날짜"),
                        fieldWithPath("result.countList").type(JsonFieldType.ARRAY).description("티켓 개수 리스트"),
                        fieldWithPath("result.countList[].targetDate").type(JsonFieldType.STRING).description("시간 (0~23)"),
                        fieldWithPath("result.countList[].count").type(JsonFieldType.NUMBER).description("해당 시간의 티켓 개수")

                    )
                ));
        }

        @Test
        @DisplayName("일간 티켓 상태별 통계 조회에 성공한다.")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.kmh")
        void getDailyTicketStatisticsSuccess() throws Exception {
            // given
            String date = "2025-01-01";
            StatisticsType type = StatisticsType.DAILY;
            TicketStatus status = TicketStatus.REQUEST;

            StatisticsByTicketStatusResponse response = new StatisticsByTicketStatusResponse(
                date,
                List.of(new StatisticsByTicketStatusResponse.TicketCount("10", 5L))
            );

            given(statisticsGetUseCase.getTicketCountStatistics(eq(date), eq(type), eq(status)))
                .willReturn(response);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders
                    .get("/api/manager/statistics/count")
                    .param("date", date)
                    .param("type", type.name())
                    .param("status", status.name())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.isSuccess").value(true),
                    jsonPath("$.code").exists(),
                    jsonPath("$.message").exists(),
                    jsonPath("$.result.baseDate").value(date),
                    jsonPath("$.result.countList").isArray(),
                    jsonPath("$.result.countList[*].targetDate").exists(),
                    jsonPath("$.result.countList[*].count").exists()
                )
                .andDo(document(
                    "Statistics/GetTicketStatistics/Success/Daily",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("result.baseDate").type(JsonFieldType.STRING).description("조회 기준 날짜"),
                        fieldWithPath("result.countList").type(JsonFieldType.ARRAY).description("티켓 개수 리스트"),
                        fieldWithPath("result.countList[].targetDate").type(JsonFieldType.STRING).description("시간 (0~23)"),
                        fieldWithPath("result.countList[].count").type(JsonFieldType.NUMBER).description("해당 시간의 티켓 개수")
                    )
                ));
        }

        @Test
        @DisplayName("올바르지 않은 StatisticsType이 들어올 경우 예외가 발생한다")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.kmh")
        void getTicketStatisticsWithInvalidType() throws Exception {
            // given
            String date = "2025-01";
            String invalidType = "INVALID_TYPE";
            TicketStatus status = TicketStatus.REQUEST;

            given(statisticsGetUseCase.getTicketCountStatistics(eq(date), any(StatisticsType.class), eq(status)))
                .willThrow(ApplicationException.from(ILLEGAL_STATISTICS_OPTION));

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders
                    .get("/api/manager/statistics/count")
                    .param("date", date)
                    .param("type", invalidType)
                    .param("status", status.name())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.type").value("about:blank"),
                    jsonPath("$.title").value("Bad Request"),
                    jsonPath("$.status").value(400),
                    jsonPath("$.detail").value("Failed to convert 'type' with value: 'INVALID_TYPE'"),
                    jsonPath("$.instance").value("/api/manager/statistics/count")
                )
                .andDo(document(
                    "Statistics/GetTicketStatistics/Failure/InvalidStatisticsType",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("type").type(JsonFieldType.STRING).description("실패 타입"),
                        fieldWithPath("title").type(JsonFieldType.STRING).description("오류 제목"),
                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("detail").type(JsonFieldType.STRING).description("응답 메시지 상세"),
                        fieldWithPath("instance").type(JsonFieldType.STRING).description("API 경로")
                    )
                ));
        }
    }
}
