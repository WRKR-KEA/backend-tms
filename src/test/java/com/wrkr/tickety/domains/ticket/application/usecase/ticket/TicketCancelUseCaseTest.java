package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.model.TicketHistory;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketUpdateService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistorySaveService;
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
class TicketCancelUseCaseTest {

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    private static final Long USER_ID = 1L;
    private static final Long TICKET_ID = 100L;

    private Member user;
    private Ticket ticket;

    @Mock
    private TicketGetService ticketGetService;

    @Mock
    private TicketUpdateService ticketUpdateService;

    @Mock
    private TicketHistorySaveService ticketHistorySaveService;

    @InjectMocks
    private TicketCancelUseCase ticketCancelUseCase;

    @BeforeEach
    void setUp() {
        user = Member.builder()
            .memberId(USER_ID)
            .nickname("사용자")
            .email("user@naver.com")
            .isDeleted(false)
            .build();

        ticket = Ticket.builder()
            .ticketId(TICKET_ID)
            .user(user)
            .title("테스트 티켓")
            .content("테스트 티켓 내용")
            .status(TicketStatus.REQUEST)
            .build();
    }

    @Test
    @DisplayName("✅ 성공: 요청 상태의 티켓을 정상적으로 취소")
    void cancelTicket_Success() {
        // given
        given(ticketGetService.getTicketByTicketId(TICKET_ID)).willReturn(ticket);
        given(ticketUpdateService.updateStatus(ticket, TicketStatus.CANCEL)).willReturn(ticket);

        // when
        TicketPkResponse response = ticketCancelUseCase.cancelTicket(USER_ID, TICKET_ID);

        // then
        assertThat(response.ticketId()).isNotNull();
        verify(ticketHistorySaveService, times(1)).save(any(TicketHistory.class));
    }

    @Test
    @DisplayName("❌ 실패: 티켓이 사용자의 것이 아닐 경우 예외 발생")
    void cancelTicket_NotBelongToUser() {
        // given
        Member anotherUser = Member.builder()
            .memberId(2L)
            .nickname("다른 사용자")
            .email("other@naver.com")
            .build();
        Ticket newTicket = Ticket.builder()
            .ticketId(TICKET_ID)
            .user(anotherUser)
            .title("테스트 티켓")
            .content("테스트 티켓 내용")
            .status(TicketStatus.REQUEST)
            .build();

        given(ticketGetService.getTicketByTicketId(TICKET_ID)).willReturn(newTicket);

        // when & then
        assertThatThrownBy(() -> ticketCancelUseCase.cancelTicket(USER_ID, TICKET_ID))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TicketErrorCode.TICKET_NOT_BELONG_TO_USER.getMessage());
    }

    @Test
    @DisplayName("❌ 실패: 티켓이 요청 상태가 아닐 경우 예외 발생")
    void cancelTicket_NotRequestStatus() {
        // given
        ticket.updateStatus(TicketStatus.IN_PROGRESS);
        given(ticketGetService.getTicketByTicketId(TICKET_ID)).willReturn(ticket);

        // when & then
        assertThatThrownBy(() -> ticketCancelUseCase.cancelTicket(USER_ID, TICKET_ID))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TicketErrorCode.TICKET_NOT_REQUEST_STATUS.getMessage());
    }

    @Test
    @DisplayName("❌ 실패: 존재하지 않는 티켓 ID로 취소 시 예외 발생")
    void cancelTicket_TicketNotFound() {
        // given
        given(ticketGetService.getTicketByTicketId(TICKET_ID))
            .willThrow(new ApplicationException(TicketErrorCode.TICKET_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> ticketCancelUseCase.cancelTicket(USER_ID, TICKET_ID))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TicketErrorCode.TICKET_NOT_FOUND.getMessage());
    }
}
