package com.wrkr.tickety.domains.log.presentation;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.docs.RestDocsSupport;
import com.wrkr.tickety.domains.log.application.dto.response.AccessLogExcelResponse;
import com.wrkr.tickety.domains.log.application.dto.response.AccessLogSearchResponse;
import com.wrkr.tickety.domains.log.application.usecase.AccessLogExcelDownloadUseCase;
import com.wrkr.tickety.domains.log.application.usecase.AccessLogSearchUseCase;
import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.excel.ExcelUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = LogController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class LogControllerTest extends RestDocsSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccessLogSearchUseCase accessLogSearchUseCase;

    @MockitoBean
    private AccessLogExcelDownloadUseCase accessLogExcelDownloadUseCase;

    @MockitoBean
    private ExcelUtil excelUtil;

    @MockitoBean
    private JwtUtils jwtUtils;

    private List<AccessLogSearchResponse> accessLogResponses;
    private List<AccessLogExcelResponse> accessLogExcelDownloadResponse;

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @BeforeEach
    void setUp() {
        accessLogResponses = List.of(
            AccessLogSearchResponse.builder()
                .accessLogId(PkCrypto.encrypt(1L))
                .nickname("user.psw")
                .ip("111.222.333.444")
                .action(ActionType.LOGIN)
                .accessAt(LocalDateTime.now())
                .isSuccess(true)
                .build(),
            AccessLogSearchResponse.builder()
                .accessLogId(PkCrypto.encrypt(2L))
                .nickname("admin.psw")
                .ip("111.222.333.555")
                .action(ActionType.LOGOUT)
                .accessAt(LocalDateTime.now())
                .isSuccess(false)
                .build()
        );

        accessLogExcelDownloadResponse = List.of(
            AccessLogExcelResponse.builder()
                .accessLogId(PkCrypto.encrypt(1L))
                .nickname("user.psw")
                .ip("111.222.333.444")
                .accessAt(LocalDateTime.now().toString())
                .action(ActionType.LOGIN)
                .isSuccess("성공")
                .build(),
            AccessLogExcelResponse.builder()
                .accessLogId(PkCrypto.encrypt(2L))
                .nickname("admin.psw")
                .ip("111.222.333.555")
                .accessAt(LocalDateTime.now().toString())
                .action(ActionType.LOGOUT)
                .isSuccess("실패")
                .build()
        );
    }

    @Override
    protected Object initController() {
        return new LogController(accessLogSearchUseCase, accessLogExcelDownloadUseCase, excelUtil);
    }

    @Nested
    @DisplayName("관리자 - 접속 로그 조회 API 테스트")
    class SearchAccessLogsTest {

        @Test
        @DisplayName("성공: 검색 조건 없이 전체 조회")
        @WithMockCustomUser(username = "admin", role = Role.ADMIN, nickname = "admin.psw")
        void searchAccessLogs_Success_NoFilters() throws Exception {
            Page<AccessLogSearchResponse> pageResponse = new PageImpl<>(accessLogResponses, Pageable.unpaged(), accessLogResponses.size());
            given(accessLogSearchUseCase.searchAccessLogs(any(), any(), any(), any(), any()))
                .willReturn(ApplicationPageResponse.of(pageResponse, Function.identity()));

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/access-logs")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("Log/searchAccessLogs/Request/Sucess1",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .build())));
        }


        @Test
        @DisplayName("성공: 특정 액션 타입으로 검색")
        @WithMockCustomUser(username = "admin", role = Role.ADMIN, nickname = "admin.psw")
        void searchAccessLogs_Success_ActionFilter() throws Exception {
            given(accessLogSearchUseCase.searchAccessLogs(any(), any(), any(), any(), any()))
                .willReturn(ApplicationPageResponse.of(new PageImpl<>(accessLogResponses), Function.identity()));

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/access-logs")
                    .param("action", "LOGIN")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("Log/searchAccessLogs/Request/Sucess2",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("로그 API")
                        .summary("특정 액션 타입(LOGIN)으로 검색 성공")
                        .build())));
        }


        @Test
        @DisplayName("성공: 특정 날짜 범위 검색")
        @WithMockCustomUser(username = "admin", role = Role.ADMIN, nickname = "admin.psw")
        void searchAccessLogs_Success_DateRange() throws Exception {
            given(accessLogSearchUseCase.searchAccessLogs(any(), any(), any(), any(), any()))
                .willReturn(ApplicationPageResponse.of(new PageImpl<>(accessLogResponses), Function.identity()));

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/access-logs")
                    .param("startDate", "2025-01-01")
                    .param("endDate", "2025-12-31")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("Log/searchAccessLogs/Request/Sucess3",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("로그 API")
                        .summary("특정 날짜 범위(2025-01-01 ~ 2025-12-31)로 접속 로그 검색 성공")
                        .build())));
        }


        @Test
        @DisplayName("실패: 유효하지 않은 날짜 범위")
        @WithMockCustomUser(username = "admin", role = Role.ADMIN, nickname = "admin.psw")
        void searchAccessLogs_InvalidDateRange() throws Exception {
            willThrow(ApplicationException.from(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID))
                .given(accessLogSearchUseCase).searchAccessLogs(any(), any(), any(), any(), any());

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/access-logs")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("Log/searchAccessLogs/Request/Failure1",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("로그 API")
                        .summary("잘못된 날짜 범위 입력")
                        .build())));
        }

        @Test
        @DisplayName("실패: 시작 날짜가 종료 날짜보다 늦음")
        @WithMockCustomUser(username = "admin", role = Role.ADMIN, nickname = "admin.psw")
        void searchAccessLogs_Fail_InvalidDateRange() throws Exception {
            willThrow(new ApplicationException(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID))
                .given(accessLogSearchUseCase).searchAccessLogs(any(), any(), any(), any(), any());

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/access-logs")
                    .param("startDate", "2025-12-31")
                    .param("endDate", "2025-01-01")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("Log/searchAccessLogs/Request/Failure2",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("로그 API")
                        .summary("실패: 시작 날짜(2025-12-31)가 종료 날짜(2025-01-01)보다 늦음")
                        .build())));
        }

        @Test
        @DisplayName("실패: 검색 결과가 없음")
        @WithMockCustomUser(username = "admin", role = Role.ADMIN, nickname = "admin.psw")
        void searchAccessLogs_Fail_NoResults() throws Exception {
            given(accessLogSearchUseCase.searchAccessLogs(any(), any(), any(), any(), any()))
                .willReturn(ApplicationPageResponse.of(new PageImpl<>(List.of()), Function.identity()));

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/access-logs")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("Log/searchAccessLogs/Request/Failure3",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("로그 API")
                        .summary("검색 결과가 없음 - 빈 리스트 반환")
                        .build())));
        }

    }

    @Nested
    @DisplayName("관리자 - 접속 로그 엑셀 다운로드 API 테스트")
    class DownloadAccessLogsExcelTest {

        @Test
        @DisplayName("성공: 검색 조건 없이 전체 조회")
        @WithMockCustomUser(username = "admin", role = Role.ADMIN, nickname = "admin.psw")
        void downloadAccessLogsExcel_Success_NoFilters() throws Exception {
            given(accessLogExcelDownloadUseCase.getAllAccessLogs(any(), any(), any(), any()))
                .willReturn(accessLogExcelDownloadResponse);

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/access-logs/excel")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("Log/downloadAccessLogsExcel/Request/Sucess1",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("로그 API")
                        .summary("검색 조건 없이 접속 로그 엑셀 다운로드 성공")
                        .build())));
        }

        @Test
        @DisplayName("성공: 특정 액션 타입으로 검색")
        @WithMockCustomUser(username = "admin", role = Role.ADMIN, nickname = "admin.psw")
        void downloadAccessLogsExcel_Success_ActionFilter() throws Exception {
            given(accessLogExcelDownloadUseCase.getAllAccessLogs(any(), any(), any(), any()))
                .willReturn(accessLogExcelDownloadResponse);

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/access-logs/excel")
                    .param("action", "LOGIN")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("Log/downloadAccessLogsExcel/Request/Sucess2",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("로그 API")
                        .summary("특정 액션 타입(LOGIN)으로 접속 로그 엑셀 다운로드 성공")
                        .build())));
        }

        @Test
        @DisplayName("성공: 특정 날짜 범위 검색")
        @WithMockCustomUser(username = "admin", role = Role.ADMIN, nickname = "admin.psw")
        void downloadAccessLogsExcel_Success_DateRange() throws Exception {
            given(accessLogExcelDownloadUseCase.getAllAccessLogs(any(), any(), any(), any()))
                .willReturn(accessLogExcelDownloadResponse);

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/access-logs/excel")
                    .param("startDate", "2025-01-01")
                    .param("endDate", "2025-12-31")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("Log/downloadAccessLogsExcel/Request/Sucess3",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("로그 API")
                        .summary("특정 날짜 범위(2025-01-01 ~ 2025-12-31)로 접속 로그 엑셀 다운로드 성공")
                        .build())));
        }


        @Test
        @DisplayName("실패: 시작 날짜가 종료 날짜보다 늦음")
        @WithMockCustomUser(username = "admin", role = Role.ADMIN, nickname = "admin.psw")
        void downloadAccessLogsExcel_Fail_InvalidDateRange() throws Exception {
            willThrow(new ApplicationException(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID))
                .given(accessLogExcelDownloadUseCase).getAllAccessLogs(any(), any(), any(), any());

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/access-logs/excel")
                    .param("startDate", "2025-12-31")
                    .param("endDate", "2025-01-01")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("Log/downloadAccessLogsExcel/Request/Failure1",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("로그 API")
                        .summary("실패: 시작 날짜(2025-12-31)가 종료 날짜(2025-01-01)보다 늦음")
                        .build())));
        }


        @Test
        @DisplayName("실패: 잘못된 날짜 형식 입력")
        @WithMockCustomUser(username = "admin", role = Role.ADMIN, nickname = "admin.psw")
        void downloadAccessLogsExcel_Fail_InvalidDateFormat() throws Exception {
            willThrow(new ApplicationException(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID))
                .given(accessLogExcelDownloadUseCase).getAllAccessLogs(any(), any(), any(), any());

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/access-logs/excel")
                    .param("startDate", "2025-13-40") // 존재하지 않는 날짜
                    .param("endDate", "2025-12-31")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("Log/downloadAccessLogsExcel/Request/Failure2",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("로그 API")
                        .summary("실패: 잘못된 날짜 형식 입력 (예: 2025-13-40)")
                        .build())));
        }

        @Test
        @DisplayName("실패: 검색 결과가 없음")
        @WithMockCustomUser(username = "admin", role = Role.ADMIN, nickname = "admin.psw")
        void downloadAccessLogsExcel_Fail_NoResults() throws Exception {
            given(accessLogExcelDownloadUseCase.getAllAccessLogs(any(), any(), any(), any()))
                .willReturn(List.of());

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/access-logs/excel")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("Log/downloadAccessLogsExcel/Request/Failure3",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(ResourceSnippetParameters.builder()
                        .tag("로그 API")
                        .summary("검색 결과가 없음 - 빈 리스트 반환")
                        .build())));
        }

    }
}
