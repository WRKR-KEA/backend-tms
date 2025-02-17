package com.wrkr.tickety.domains.ticket.presentation;

import static com.wrkr.tickety.common.fixture.ticket.TicketFixture.TICKET_CANCEL_01;
import static com.wrkr.tickety.common.fixture.ticket.TicketFixture.TICKET_COMPLETE_01;
import static com.wrkr.tickety.common.fixture.ticket.TicketFixture.TICKET_REQUEST_01;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.auth.exception.AuthErrorCode;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.TicketDelegateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.ManagerTicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper;
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
import com.wrkr.tickety.domains.ticket.domain.constant.SortType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.excel.ExcelUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    private final static Long TICKET_ID = 1L;
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
            Ticket ticket = TICKET_CANCEL_01.toInProgressTicket();

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
            Ticket ticket = TICKET_COMPLETE_01.toInProgressTicket();
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

    @Nested
    @DisplayName("담당자 티켓 승인 API [PATCH /api/manager/tickets/approve?ticketId=abc123&ticketId=def456]")
    class approveTicket {

        @Test
        @DisplayName("티켓 승인 처리에 성공한다.")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.kjw", memberId = 2L)
        void approveTicketSuccess() throws Exception {
            // given
            final List<TicketPkResponse> response = List.of(new TicketPkResponse(PkCrypto.encrypt(TICKET_ID)));
            doReturn(response).when(ticketApproveUseCase).approveTicket(anyLong(), anyList());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                .patch("/api/manager/tickets/approve")
                .queryParam("ticketId", PkCrypto.encrypt(TICKET_ID))
                //.header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                   .andExpect(status().isOk())
                   .andDo(
                       document(
                           "ManagerTicketApi/Approve/Success",
                           preprocessRequest(prettyPrint()),
                           preprocessResponse(prettyPrint()),
//                        requestHeaders(
//                            headerWithName(AUTHORIZATION).description("Access Token")
//                        ),
                           queryParameters(
                               parameterWithName("ticketId").description("승인할 티켓 ID(PK) 리스트")
                                          ),
                           responseFields(
                               fieldWithPath("isSuccess").description("성공 여부"),
                               fieldWithPath("code").description("커스텀 예외 코드"),
                               fieldWithPath("message").description("예외 메시지"),
                               fieldWithPath("result").description("반환 결과"),
                               fieldWithPath("result[].ticketId").description("승인된 티켓의 ID(PK) 리스트")
                                         )
                               )
                         );
        }

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 티켓 승인 처리에 실패한다.")
//        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.kjw", memberId = 2L)
        void withoutAccessToken() throws Exception {
            // given
            final List<TicketPkResponse> response = List.of(new TicketPkResponse(PkCrypto.encrypt(TICKET_ID)));
            doReturn(response).when(ticketApproveUseCase).approveTicket(anyLong(), anyList());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                .patch("/api/manager/tickets/approve")
                .queryParam("ticketId", PkCrypto.encrypt(TICKET_ID))
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
                           "ManagerTicketApi/Approve/Failure/Case1",
                           preprocessRequest(prettyPrint()),
                           preprocessResponse(prettyPrint()),
                           queryParameters(
                               parameterWithName("ticketId").description("승인할 티켓 ID(PK) 리스트")
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
        @DisplayName("승인하는 회원의 권한이 담당자가 아니면 티켓 승인 처리에 실패한다.")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "user.kjw", memberId = 2L)
        void throwExceptionByMemberNotAllowed() throws Exception {
            // given
            Ticket ticket = TICKET_REQUEST_01.toRequestTicket();

            doThrow(ApplicationException.from(MemberErrorCode.MEMBER_NOT_ALLOWED))
                .when(ticketApproveUseCase)
                .approveTicket(anyLong(), anyList());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                .patch("/api/manager/tickets/approve")
                .queryParam("ticketId", PkCrypto.encrypt(ticket.getTicketId()))
                //.header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);

            // then
            final MemberErrorCode expectedError = MemberErrorCode.MEMBER_NOT_ALLOWED;
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
                           "ManagerTicketApi/Approve/Failure/Case2",
                           preprocessRequest(prettyPrint()),
                           preprocessResponse(prettyPrint()),
                           queryParameters(
                               parameterWithName("ticketId").description("승인할 티켓 ID(PK) 리스트")
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
        @DisplayName("존재하지 않는 티켓이면 티켓 승인 처리에 실패한다.")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.kjw", memberId = 2L)
        void throwExceptionByTicketNotFound() throws Exception {
            // given
            doThrow(ApplicationException.from(TicketErrorCode.TICKET_NOT_FOUND))
                .when(ticketApproveUseCase)
                .approveTicket(anyLong(), anyList());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                .patch("/api/manager/tickets/approve")
                .queryParam("ticketId", PkCrypto.encrypt(TICKET_ID))
                //.header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN)
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
                           "ManagerTicketApi/Approve/Failure/Case3",
                           preprocessRequest(prettyPrint()),
                           preprocessResponse(prettyPrint()),
                           queryParameters(
                               parameterWithName("ticketId").description("승인할 티켓 ID(PK) 리스트")
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
        @DisplayName("요청 상태인 티켓이 아니면 티켓 승인 처리에 실패한다.")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.kjw", memberId = 2L)
        void throwExceptionByTicketNotApprovable() throws Exception {
            // given
            Ticket ticket = TICKET_COMPLETE_01.toInProgressTicket();

            doThrow(ApplicationException.from(TicketErrorCode.TICKET_NOT_APPROVABLE))
                .when(ticketApproveUseCase)
                .approveTicket(anyLong(), anyList());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                .patch("/api/manager/tickets/approve")
                .queryParam("ticketId", PkCrypto.encrypt(ticket.getTicketId()))
                //.header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);

            // then
            final TicketErrorCode expectedError = TicketErrorCode.TICKET_NOT_APPROVABLE;
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
                           "ManagerTicketApi/Approve/Failure/Case4",
                           preprocessRequest(prettyPrint()),
                           preprocessResponse(prettyPrint()),
                           queryParameters(
                               parameterWithName("ticketId").description("승인할 티켓 ID(PK) 리스트")
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

    @Nested
    class managerTicketAllGetTest {

        static List<Ticket> ticketList;

        @BeforeAll
        static void setUp() {
            Category parent = Category.builder().categoryId(1L).name("parent").build();
            Category category = Category.builder().categoryId(1L).name("category").parent(parent).build();
            Member user = Member.builder().memberId(1L).nickname("user.hjw").build();

            ticketList = new ArrayList<>();
            ticketList.add(Ticket.builder().ticketId(1L).category(category).serialNumber("1").title("title").content("content").status(TicketStatus.IN_PROGRESS)
                                 .isPinned(true).user(user).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build());
            ticketList.add(Ticket.builder().ticketId(2L).category(category).serialNumber("2").title("title").content("content").status(TicketStatus.IN_PROGRESS)
                                 .isPinned(true).user(user).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build());
            ticketList.add(Ticket.builder().ticketId(3L).category(category).serialNumber("3").title("title").content("content").status(TicketStatus.IN_PROGRESS)
                                 .isPinned(true).user(user).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build());
            ticketList.add(Ticket.builder().ticketId(4L).category(category).serialNumber("4").title("title").content("content").status(TicketStatus.IN_PROGRESS)
                                 .isPinned(true).user(user).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build());
        }

        @Test
        @DisplayName("담당자가 담당하고 있는 티켓 목록을 조회한다.")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "thama.kakao", memberId = 11L)
        void testGetManagerTickets() throws Exception {
            //given
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Member member = (Member) authentication.getPrincipal();
            Function<Ticket, ManagerTicketAllGetResponse> converter = TicketMapper::toManagerTicketAllGetResponse;
            PageRequest pageRequest = PageRequest.of(0, 20);
            ApplicationPageRequest applicationPageRequest = new ApplicationPageRequest(1, 20, SortType.NEWEST);
            Page<Ticket> page = new PageImpl<>(ticketList, pageRequest, ticketList.size());
            ApplicationPageResponse<ManagerTicketAllGetResponse> dummyPageResponse = ApplicationPageResponse.of(page, converter);

            given(managerTicketAllGetUseCase.getManagerTicketList(member.getMemberId(), applicationPageRequest, TicketStatus.IN_PROGRESS, "query"))
                .willReturn(dummyPageResponse);

            //when & then
            mockMvc.perform(get("/api/manager/tickets")
                                .queryParam("status", TicketStatus.IN_PROGRESS.name())
                                .queryParam("query", "query")
                                .queryParam("page", "1")
                                .queryParam("size", "20")
                                .queryParam("sortType", SortType.NEWEST.name())
                                .with(csrf()))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.isSuccess").value(true))
                   .andExpect(jsonPath("$.result.elements").isArray())
                   .andDo(document("ManagerTicketApi/GetManagerTickets/success",
                                   preprocessRequest(prettyPrint()),
                                   preprocessResponse(prettyPrint()),
                                   queryParameters(
                                       parameterWithName("status").description("티켓 상태"),
                                       parameterWithName("query").description("검색어"),
                                       parameterWithName("page").description("페이지 번호"),
                                       parameterWithName("size").description("페이지 크기"),
                                       parameterWithName("sortType").description("정렬 타입 (NEWEST | OLDEST | UPDATED)")
                                                  ),
                                   responseFields(
                                       fieldWithPath("isSuccess").description("성공 여부"),
                                       fieldWithPath("code").description("응답 코드"),
                                       fieldWithPath("message").description("응답 메시지"),
                                       fieldWithPath("result").description("응답 결과"),
                                       fieldWithPath("result.elements").description("티켓 목록"),
                                       fieldWithPath("result.elements[].id").description("티켓 ID"),
                                       fieldWithPath("result.elements[].serialNumber").description("티켓 일련번호"),
                                       fieldWithPath("result.elements[].title").description("티켓 제목"),
                                       fieldWithPath("result.elements[].firstCategory").description("1차 카테고리"),
                                       fieldWithPath("result.elements[].secondCategory").description("2차 카테고리"),
                                       fieldWithPath("result.elements[].status").description("티켓 상태"),
                                       fieldWithPath("result.elements[].requesterNickname").description("요청자 닉네임"),
                                       fieldWithPath("result.elements[].createdAt").description("생성일"),
                                       fieldWithPath("result.elements[].updatedAt").description("수정일"),
                                       fieldWithPath("result.elements[].isPinned").description("고정 여부"),
                                       fieldWithPath("result.currentPage").description("현재 페이지 번호"),
                                       fieldWithPath("result.totalPages").description("전체 페이지 수"),
                                       fieldWithPath("result.totalElements").description("전체 티켓 수"),
                                       fieldWithPath("result.size").description("페이지 크기")
                                                 )
                                  )
                         );
        }

        @Test
        @DisplayName("페이지에 문자값이 입력되었을 때 예외를 반환한다.")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "thama.kakao", memberId = 11L)
        void testGetManagerTicketsWhenInvalidPage() throws Exception {
            //when & then
            mockMvc.perform(get("/api/manager/tickets")
                                .queryParam("status", TicketStatus.IN_PROGRESS.name())
                                .queryParam("query", "query")
                                .queryParam("page", "dfaesf")
                                .queryParam("size", "20")
                                .queryParam("sortType", SortType.NEWEST.name())
                                .with(csrf()))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.isSuccess").value(false))
                   .andDo(document("ManagerTicketApi/GetManagerTickets/failure/case1",
                                   preprocessRequest(prettyPrint()),
                                   preprocessResponse(prettyPrint()),
                                   queryParameters(
                                       parameterWithName("status").description("티켓 상태"),
                                       parameterWithName("query").description("검색어"),
                                       parameterWithName("page").description("페이지 번호"),
                                       parameterWithName("size").description("페이지 크기"),
                                       parameterWithName("sortType").description("정렬 타입 (NEWEST | OLDEST | UPDATED)")
                                                  ),
                                   responseFields(
                                       fieldWithPath("isSuccess").description("성공 여부"),
                                       fieldWithPath("code").description("응답 코드"),
                                       fieldWithPath("message").description("응답 메시지(잘못된 요청입니다.)"),
                                       fieldWithPath("result").description("응답 결과"),
                                       fieldWithPath("result.page").description("오류 메세지(Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer')")
                                                 )
                                  )
                         );
        }

        @Test
        @DisplayName("목록 크기로 문자값이 입력되었을 때 예외를 반환한다.")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "thama.kakao", memberId = 11L)
        void testGetManagerTicketsWhenInvalidSize() throws Exception {
            //when & then
            mockMvc.perform(get("/api/manager/tickets")
                                .queryParam("status", TicketStatus.IN_PROGRESS.name())
                                .queryParam("query", "query")
                                .queryParam("page", "1")
                                .queryParam("size", "dfaesf")
                                .queryParam("sortType", SortType.NEWEST.name())
                                .with(csrf()))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.isSuccess").value(false))
                   .andDo(document("ManagerTicketApi/GetManagerTickets/failure/case2",
                                   preprocessRequest(prettyPrint()),
                                   preprocessResponse(prettyPrint()),
                                   queryParameters(
                                       parameterWithName("status").description("티켓 상태"),
                                       parameterWithName("query").description("검색어"),
                                       parameterWithName("page").description("페이지 번호"),
                                       parameterWithName("size").description("페이지 크기"),
                                       parameterWithName("sortType").description("정렬 타입 (NEWEST | OLDEST | UPDATED)")
                                                  ),
                                   responseFields(
                                       fieldWithPath("isSuccess").description("성공 여부"),
                                       fieldWithPath("code").description("응답 코드"),
                                       fieldWithPath("message").description("응답 메시지(잘못된 요청입니다.)"),
                                       fieldWithPath("result").description("응답 결과"),
                                       fieldWithPath("result.size").description("오류 메세지(Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer')")
                                                 )
                                  )
                         );
        }
    }
}
