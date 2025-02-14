package com.wrkr.tickety.domains.ticket.presentation;

import static com.wrkr.tickety.common.fixture.ticket.TicketFixture.TICKET_CANCEL_01;
import static com.wrkr.tickety.common.fixture.ticket.TicketFixture.TICKET_COMPLETE_01;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.auth.exception.AuthErrorCode;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.TicketDelegateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.DepartmentTicketAllGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerGetMainUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketAllGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketDelegateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketDetailUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketPinUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketAllGetToExcelUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketApproveUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketCompleteUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketRejectUseCase;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.excel.ExcelUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(controllers = ManagerTicketController.class)
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
class ManagerTicketControllerTest {

    private static final Logger log = LoggerFactory.getLogger(ManagerTicketControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ManagerTicketDelegateUseCase managerTicketDelegateUseCase;

    @MockitoBean
    private ManagerTicketDetailUseCase managerTicketDetailUseCase;

    @MockitoBean
    private TicketApproveUseCase ticketApproveUseCase;

    @MockitoBean
    private TicketRejectUseCase ticketRejectUseCase;

    @MockitoBean
    private TicketCompleteUseCase ticketCompleteUseCase;

    @MockitoBean
    private DepartmentTicketAllGetUseCase departmentTicketAllGetUseCase;

    @MockitoBean
    private ManagerTicketAllGetUseCase managerTicketAllGetUseCase;

    @MockitoBean
    private ManagerTicketPinUseCase managerTicketPinUseCase;

    @MockitoBean
    private TicketAllGetToExcelUseCase ticketAllGetToExcelUseCase;

    @MockitoBean
    private ExcelUtil excelUtil;

    @MockitoBean
    private ManagerGetMainUseCase managerGetMainUseCase;

    @MockitoBean
    private JwtUtils jwtUtils;

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    private final static Long TICKET_ID = 1L;

    @Nested
    @DisplayName("담당자 변경 API 테스트")
    class DelegateTicketTest {

        @Test
        @DisplayName("성공: 티켓 담당자 변경")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw", memberId = 2L)
        void delegateTicket_Success() throws Exception {
            // Given
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Member member = (Member) authentication.getPrincipal();
            log.info("customUserDetails : {}", member.getNickname());

            String ticketId = PkCrypto.encrypt(1L);
            TicketDelegateRequest request = new TicketDelegateRequest(PkCrypto.encrypt(2L));
            TicketPkResponse response = new TicketPkResponse(ticketId);

            when(managerTicketDelegateUseCase.delegateTicket(1L, 2L, request))
                .thenReturn(response);

            // When & Then
            mockMvc.perform(patch("/api/manager/tickets/{ticketId}/delegate", ticketId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("실패: 티켓을 찾을 수 없음")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw", memberId = 2L)
        void delegateTicket_TicketNotFound() throws Exception {
            // Given
            String ticketId = PkCrypto.encrypt(1L);
            TicketDelegateRequest request = new TicketDelegateRequest(PkCrypto.encrypt(2L));

            doThrow(new ApplicationException(TicketErrorCode.TICKET_NOT_FOUND))
                .when(managerTicketDelegateUseCase)
                .delegateTicket(anyLong(), anyLong(), any(TicketDelegateRequest.class));

            // When & Then
            mockMvc.perform(patch("/api/manager/tickets/{ticketId}/delegate", ticketId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("실패: 현재 담당자가 일치하지 않음")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw", memberId = 2L)
        void delegateTicket_ManagerNotMatch() throws Exception {
            // Given
            String ticketId = PkCrypto.encrypt(1L);
            TicketDelegateRequest request = new TicketDelegateRequest(PkCrypto.encrypt(2L));

            doThrow(new com.wrkr.tickety.global.exception.ApplicationException(TicketErrorCode.TICKET_MANAGER_NOT_MATCH))
                .when(managerTicketDelegateUseCase)
                .delegateTicket(anyLong(), anyLong(), any(TicketDelegateRequest.class));

            // When & Then
            mockMvc.perform(patch("/api/manager/tickets/{ticketId}/delegate", ticketId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("실패: 위임할 수 없는 상태의 티켓")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw", memberId = 2L)
        void delegateTicket_NotDelegatable() throws Exception {
            // Given
            String ticketId = PkCrypto.encrypt(1L);
            TicketDelegateRequest request = new TicketDelegateRequest(PkCrypto.encrypt(2L));

            doThrow(new com.wrkr.tickety.global.exception.ApplicationException(TicketErrorCode.TICKET_NOT_DELEGATABLE))
                .when(managerTicketDelegateUseCase)
                .delegateTicket(anyLong(), anyLong(), any(TicketDelegateRequest.class));

            // When & Then
            mockMvc.perform(patch("/api/manager/tickets/{ticketId}/delegate", ticketId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("담당자 티켓 완료 API [PATCH /api/manager/tickets/{ticketId}/complete]")
    class completeTicket {

        @Test
        @DisplayName("티켓 완료 처리에 성공한다.")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw", memberId = 2L)
        void completeTicketSuccess() throws Exception {
            // given
            final TicketPkResponse response = new TicketPkResponse(PkCrypto.encrypt(TICKET_ID));
            doReturn(response).when(ticketCompleteUseCase).completeTicket(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                .patch("/api/manager/tickets/{ticketId}/complete", PkCrypto.encrypt(TICKET_ID))
//                .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(
                    document(
                        "ManagerTicketApi/Complete/Success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
//                        requestHeaders(
//                            headerWithName(AUTHORIZATION).description("Access Token")
//                        ),
                        pathParameters(
                            parameterWithName("ticketId").description("완료할 티켓 ID(PK)")
                        ),
                        responseFields(
                            fieldWithPath("isSuccess").description("성공 여부"),
                            fieldWithPath("code").description("커스텀 예외 코드"),
                            fieldWithPath("message").description("예외 메시지"),
                            fieldWithPath("result").description("완료한 티켓 정보"),
                            fieldWithPath("result.ticketId").description("완료한 티켓 ID(PK)")
                        )
                    )
                );
        }

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 티켓 완료 처리에 실패한다.")
//        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw", memberId = 2L)
        void withoutAccessToken() throws Exception {
            // given
            final TicketPkResponse response = new TicketPkResponse(PkCrypto.encrypt(TICKET_ID));
            doReturn(response).when(ticketCompleteUseCase).completeTicket(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                .patch("/api/manager/tickets/{ticketId}/complete", PkCrypto.encrypt(TICKET_ID))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.AUTHENTICATION_FAILED;
            mockMvc.perform(requestBuilder)
                .andExpect(
                    status().isUnauthorized()
                )
                .andDo(
                    document(
                        "ManagerTicketApi/Complete/Failure/Case1",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("ticketId").description("완료할 티켓 ID(PK)")
                        )
//                        responseFields(
//                            fieldWithPath("isSuccess").description("성공 여부"),
//                            fieldWithPath("code").description("커스텀 예외 코드"),
//                            fieldWithPath("message").description("예외 메시지")
//                        )
                    )
                );
        }

        @Test
        @DisplayName("해당 티켓의 담당자가 아니면 티켓 완료 처리에 실패한다.")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw", memberId = 2L)
        void throwExceptionByTicketManagerNotMatch() throws Exception {
            // given
            Ticket ticket = TICKET_CANCEL_01.toTicket();

            doThrow(ApplicationException.from(TicketErrorCode.TICKET_MANAGER_NOT_MATCH))
                .when(ticketCompleteUseCase)
                .completeTicket(anyLong(), eq(ticket.getTicketId()));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                .patch("/api/manager/tickets/{ticketId}/complete", PkCrypto.encrypt(ticket.getTicketId()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);

            // then
            final TicketErrorCode expectedError = TicketErrorCode.TICKET_MANAGER_NOT_MATCH;
            mockMvc.perform(requestBuilder)
                .andExpectAll(
                    status().isForbidden(),
                    jsonPath("$.isSuccess").exists(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").exists(),
                    jsonPath("$.code").value(expectedError.getCustomCode()),
                    jsonPath("$.message").exists(),
                    jsonPath("$.message").value(expectedError.getMessage())
                )
                .andDo(
                    document(
                        "ManagerTicketApi/Complete/Failure/Case2",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("ticketId").description("완료할 티켓 ID(PK)")
                        ),
                        responseFields(
                            fieldWithPath("isSuccess").description("성공 여부"),
                            fieldWithPath("code").description("커스텀 예외 코드"),
                            fieldWithPath("message").description("예외 메시지")
                        )
                    )
                );
        }

        @Test
        @DisplayName("존재하지 않는 티켓이면 티켓 완료 처리에 실패한다.")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw", memberId = 2L)
        void throwExceptionByTicketNotFound() throws Exception {
            // given
            doThrow(ApplicationException.from(TicketErrorCode.TICKET_NOT_FOUND))
                .when(ticketCompleteUseCase)
                .completeTicket(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                .patch("/api/manager/tickets/{ticketId}/complete", PkCrypto.encrypt(TICKET_ID))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);

            // then
            final TicketErrorCode expectedError = TicketErrorCode.TICKET_NOT_FOUND;
            mockMvc.perform(requestBuilder)
                .andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.isSuccess").exists(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").exists(),
                    jsonPath("$.code").value(expectedError.getCustomCode()),
                    jsonPath("$.message").exists(),
                    jsonPath("$.message").value(expectedError.getMessage())
                )
                .andDo(
                    document(
                        "ManagerTicketApi/Complete/Failure/Case3",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("ticketId").description("완료할 티켓 ID(PK)")
                        ),
                        responseFields(
                            fieldWithPath("isSuccess").description("성공 여부"),
                            fieldWithPath("code").description("커스텀 예외 코드"),
                            fieldWithPath("message").description("예외 메시지")
                        )
                    )
                );
        }

        @Test
        @DisplayName("진행 중인 티켓이 아니면 티켓 완료 처리에 실패한다.")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw", memberId = 2L)
        void throwExceptionByTicketNotCompletable() throws Exception {
            // given
            Ticket ticket = TICKET_COMPLETE_01.toTicket();
            doThrow(ApplicationException.from(TicketErrorCode.TICKET_NOT_COMPLETABLE))
                .when(ticketCompleteUseCase)
                .completeTicket(anyLong(), eq(ticket.getTicketId()));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                .patch("/api/manager/tickets/{ticketId}/complete", PkCrypto.encrypt(ticket.getTicketId()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);

            // then
            final TicketErrorCode expectedError = TicketErrorCode.TICKET_NOT_COMPLETABLE;
            mockMvc.perform(requestBuilder)
                .andExpectAll(
                    status().isConflict(),
                    jsonPath("$.isSuccess").exists(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").exists(),
                    jsonPath("$.code").value(expectedError.getCustomCode()),
                    jsonPath("$.message").exists(),
                    jsonPath("$.message").value(expectedError.getMessage())
                )
                .andDo(
                    document(
                        "ManagerTicketApi/Complete/Failure/Case4",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("ticketId").description("완료할 티켓 ID(PK)")
                        ),
                        responseFields(
                            fieldWithPath("isSuccess").description("성공 여부"),
                            fieldWithPath("code").description("커스텀 예외 코드"),
                            fieldWithPath("message").description("예외 메시지")
                        )
                    )
                );
        }
    }
}
