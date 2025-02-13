package com.wrkr.tickety.domains.ticket.presentation;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ManagerTicketController.class)
@AutoConfigureMockMvc
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

}
