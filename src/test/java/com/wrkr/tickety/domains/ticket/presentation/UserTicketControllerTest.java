package com.wrkr.tickety.domains.ticket.presentation;

import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketAllGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketCancelUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketDetailGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketGetMainUseCase;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserTicketController.class)
@AutoConfigureMockMvc
class UserTicketControllerTest {

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    private static final Long USER_ID = 1L;
    private static final Long TICKET_ID = 100L;

    private Member user;
    private TicketCreateRequest validRequest;
    private String encryptedCategoryId;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TicketCreateUseCase ticketCreateUseCase;

    @MockitoBean
    private TicketCancelUseCase ticketCancelUseCase;

    @MockitoBean
    private TicketAllGetUseCase ticketAllGetUseCase;

    @MockitoBean
    private TicketDetailGetUseCase ticketDetailGetUseCase;

    @MockitoBean
    private TicketGetMainUseCase ticketGetMainUseCase;


    @MockitoBean
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        encryptedCategoryId = PkCrypto.encrypt(TICKET_ID);

        user = Member.builder()
            .memberId(USER_ID)
            .nickname("사용자")
            .email("user@naver.com")
            .role(Role.USER)
            .isDeleted(false)
            .build();

        validRequest = TicketCreateRequest.builder()
            .title("테스트 티켓")
            .content("티켓 요청 내용")
            .categoryId(PkCrypto.encrypt(2L))
            .build();
    }

    @Nested
    @DisplayName("사용자 티켓 요청 API 테스트")
    class CreateTicketTest {

        @Test
        @DisplayName("✅ 성공: 티켓 요청 성공")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "manager.psw", memberId = 1L)
        void createTicket_Success() throws Exception {
            // given
            TicketPkResponse response = TicketPkResponse.builder()
                .ticketId(encryptedCategoryId)
                .build();

            given(ticketCreateUseCase.createTicket(validRequest, USER_ID)).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/user/tickets")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("❌ 실패: 존재하지 않는 카테고리로 요청 시 예외 발생")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "manager.psw", memberId = 1L)
        void createTicket_CategoryNotFound() throws Exception {
            // given
            doThrow(ApplicationException.from(CategoryErrorCode.CATEGORY_NOT_EXISTS))
                .when(ticketCreateUseCase)
                .createTicket(validRequest, USER_ID);

            // when & then
            mockMvc.perform(post("/api/user/tickets")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("❌ 실패: 존재하지 않는 사용자 ID로 생성 시 예외 발생")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "manager.psw", memberId = 1L)
        void createTicket_UserNotFound() throws Exception {
            // given
            doThrow(ApplicationException.from(MemberErrorCode.MEMBER_NOT_FOUND))
                .when(ticketCreateUseCase)
                .createTicket(validRequest, USER_ID);

            // when & then
            mockMvc.perform(post("/api/user/tickets")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("사용자 티켓 취소 API 테스트")
    class CancelTicketTest {

        @Test
        @DisplayName("✅ 성공: 사용자가 요청한 티켓을 취소")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "manager.psw", memberId = 1L)
        void cancelTicket_Success() throws Exception {
            // given
            TicketPkResponse response = TicketPkResponse.builder()
                .ticketId(encryptedCategoryId)
                .build();

            given(ticketCancelUseCase.cancelTicket(USER_ID, TICKET_ID)).willReturn(response);

            // when & then
            mockMvc.perform(patch("/api/user/tickets/{ticketId}", encryptedCategoryId)
                    .with(csrf()))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("❌ 실패: 티켓이 요청한 사용자에게 속하지 않음")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "manager.psw", memberId = 1L)
        void cancelTicket_NotBelongToUser() throws Exception {
            // given
            doThrow(ApplicationException.from(TicketErrorCode.TICKET_NOT_BELONG_TO_USER))
                .when(ticketCancelUseCase)
                .cancelTicket(USER_ID, TICKET_ID);

            // when & then
            mockMvc.perform(patch("/api/user/tickets/{ticketId}", encryptedCategoryId)
                    .with(csrf()))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("❌ 실패: 요청 상태가 아닌 티켓 취소 시 예외 발생")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "manager.psw", memberId = 1L)
        void cancelTicket_NotRequestStatus() throws Exception {
            // given
            doThrow(ApplicationException.from(TicketErrorCode.TICKET_NOT_REQUEST_STATUS))
                .when(ticketCancelUseCase)
                .cancelTicket(USER_ID, TICKET_ID);

            // when & then
            mockMvc.perform(patch("/api/user/tickets/{ticketId}", encryptedCategoryId)
                    .with(csrf()))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("❌ 실패: 존재하지 않는 티켓 ID로 취소 시 예외 발생")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "manager.psw", memberId = 1L)
        void cancelTicket_TicketNotFound() throws Exception {
            // given
            doThrow(ApplicationException.from(TicketErrorCode.TICKET_NOT_FOUND))
                .when(ticketCancelUseCase)
                .cancelTicket(USER_ID, TICKET_ID);

            // when & then
            mockMvc.perform(patch("/api/user/tickets/{ticketId}", encryptedCategoryId)
                    .with(csrf()))
                .andExpect(status().isNotFound());
        }
    }
}
