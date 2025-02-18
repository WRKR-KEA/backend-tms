package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.TicketPinRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.ManagerPinTicketResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketUpdateService;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManagerTicketPinUseCaseTest {

    @Mock
    private TicketGetService ticketGetService;

    @Mock
    private TicketUpdateService ticketUpdateService;

    @InjectMocks
    private ManagerTicketPinUseCase managerTicketPinUseCase;

    private Member testManager;
    private Ticket testTicket;
    private TicketPinRequest validRequest;
    private String encryptedTicketId;

    @BeforeEach
    void setUp() {
        testManager = Member.builder()
            .memberId(1L)
            .nickname("test_manager")
            .build();

        testTicket = Ticket.builder()
            .ticketId(2L)
            .manager(testManager)
            .isPinned(false)
            .build();

        encryptedTicketId = PkCrypto.encrypt(2L);

        validRequest = TicketPinRequest.builder()
            .ticketId(encryptedTicketId)
            .build();
    }

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Test
    @DisplayName("성공 - 티켓 고정 및 고정 해제")
    void pinTicket_Success() {
        // given
        given(ticketGetService.getTicketByTicketId(anyLong())).willReturn(testTicket);
        given(ticketUpdateService.pinTicket(testTicket)).willReturn(testTicket);

        // when
        ManagerPinTicketResponse response = managerTicketPinUseCase.pinTicket(testManager, validRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.ticketId()).isEqualTo(encryptedTicketId);
    }

    @Test
    @DisplayName("실패 - 티켓 담당자가 아닌 경우 예외 발생")
    void pinTicket_Fail_NotManager() {
        // given
        Member anotherManager = Member.builder()
            .memberId(999L)
            .nickname("another_manager")
            .build();

        given(ticketGetService.getTicketByTicketId(anyLong())).willReturn(testTicket);

        // when & then
        assertThatThrownBy(() -> managerTicketPinUseCase.pinTicket(anotherManager, validRequest))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TicketErrorCode.TICKET_MANAGER_NOT_MATCH.getMessage());
    }

    @Test
    @DisplayName("실패 - 티켓 핀 개수가 초과됨")
    void pinTicket_Fail_PinLimitExceeded() {
        // given
        given(ticketGetService.countPinTickets(anyLong())).willReturn(10L);
        given(ticketGetService.isPinTicket(anyLong())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> managerTicketPinUseCase.pinTicket(testManager, validRequest))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TicketErrorCode.TICKET_PIN_COUNT_OVER.getMessage());
    }

    @Test
    @DisplayName("실패 - 티켓이 존재하지 않는 경우 예외 발생")
    void pinTicket_Fail_TicketNotFound() {
        // given
        given(ticketGetService.getTicketByTicketId(anyLong()))
            .willThrow(ApplicationException.from(TicketErrorCode.TICKET_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> managerTicketPinUseCase.pinTicket(testManager, validRequest))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TicketErrorCode.TICKET_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("실패 - 티켓이 요청 상태가 아닌 경우 예외 발생")
    void pinTicket_Fail_NotRequested() {
        // given
        Ticket testTicketNotRequested = Ticket.builder()
            .ticketId(2L)
            .manager(null)
            .isPinned(false)
            .build();

        given(ticketGetService.getTicketByTicketId(anyLong())).willReturn(testTicketNotRequested);

        // when & then
        assertThatThrownBy(() -> managerTicketPinUseCase.pinTicket(testManager, validRequest))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TicketErrorCode.TICKET_MANAGER_NOT_FOUND.getMessage());
    }
}
