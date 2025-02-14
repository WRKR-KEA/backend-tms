package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static com.wrkr.tickety.common.fixture.member.UserFixture.MANAGER_A;
import static com.wrkr.tickety.common.fixture.member.UserFixture.MANAGER_B;
import static com.wrkr.tickety.common.fixture.member.UserFixture.MANAGER_C;
import static com.wrkr.tickety.common.fixture.ticket.TicketFixture.TICKET_IN_PROGRESS_01;
import static com.wrkr.tickety.common.fixture.ticket.TicketFixture.TICKET_REQUEST_01;
import static com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus.COMPLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.event.TicketStatusChangeEvent;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.model.TicketHistory;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketUpdateService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistorySaveService;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.event.RecordApplicationEvents;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RecordApplicationEvents
class TicketCompleteUseCaseTest {

    @Mock
    private TicketGetService ticketGetService;

    @Mock
    private TicketUpdateService ticketUpdateService;

    @Mock
    private TicketHistorySaveService ticketHistorySaveService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private TicketCompleteUseCase ticketCompleteUseCase;

    @Mock
    private PkCrypto pkCrypto;

    @BeforeEach
    public void setUp() {
        pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Nested
    @DisplayName("TicketComplete UseCase Layer > 담당자 티켓 완료")
    class completeTicket {

        @Test
        @DisplayName("티켓의 담당자가 아니라면 TICKET_MANAGER_NOT_MATCH 예외가 발생한다.")
        void throwExceptionByTicketManagerNotMatch() {
            // given
            Ticket ticket = TICKET_IN_PROGRESS_01.toTicket();
            Member manager = MANAGER_B.toMember();

            given(ticketGetService.getTicketByTicketId(ticket.getTicketId())).willReturn(ticket);

            // when - then
            assertThatThrownBy(() -> ticketCompleteUseCase.completeTicket(manager.getMemberId(), ticket.getTicketId()))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(TicketErrorCode.TICKET_MANAGER_NOT_MATCH.getMessage());

            verify(ticketGetService, times(1)).getTicketByTicketId(ticket.getTicketId());
            verifyNoMoreInteractions(ticketUpdateService, ticketHistorySaveService, applicationEventPublisher);
        }

        @Test
        @DisplayName("진행 중인 티켓이 아니라면 TICKET_NOT_COMPLETABLE 예외가 발생한다.")
        void throwExceptionByTicketNotCompletable() {
            // given
            Ticket ticket = TICKET_REQUEST_01.toTicket();
            Member manager = MANAGER_C.toMember();
            given(ticketGetService.getTicketByTicketId(ticket.getTicketId())).willReturn(ticket);

            // when - then
            assertThatThrownBy(() -> ticketCompleteUseCase.completeTicket(manager.getMemberId(), ticket.getTicketId()))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(TicketErrorCode.TICKET_NOT_COMPLETABLE.getMessage());

            verify(ticketGetService, times(1)).getTicketByTicketId(ticket.getTicketId());

            verifyNoMoreInteractions(ticketUpdateService, ticketHistorySaveService, applicationEventPublisher);
        }

        @Test
        @DisplayName("담당자는 진행 중이던 티켓 완료 처리에 성공한다.")
        void successCompleteTicket() {
            // given
            Ticket ticket = TICKET_IN_PROGRESS_01.toTicket();
            Member manager = MANAGER_A.toMember();

            given(ticketGetService.getTicketByTicketId(ticket.getTicketId())).willReturn(ticket);
            given(ticketUpdateService.updateStatus(ticket, TicketStatus.COMPLETE)).willReturn(ticket);
            willDoNothing().given(applicationEventPublisher).publishEvent(ArgumentMatchers.any(TicketStatusChangeEvent.class));

            // when
            TicketPkResponse response = ticketCompleteUseCase.completeTicket(manager.getMemberId(), ticket.getTicketId());

            // then
            assertThat(response).isNotNull();
            assertThat(ticket.getManager().getMemberId()).isEqualTo(manager.getMemberId());

            verify(ticketGetService, times(1)).getTicketByTicketId(ticket.getTicketId());
            verify(ticketUpdateService, times(1)).updateStatus(ticket, COMPLETE);
            verify(ticketHistorySaveService).save(ArgumentMatchers.any(TicketHistory.class));
            verify(applicationEventPublisher, times(1)).publishEvent(ArgumentMatchers.any(TicketStatusChangeEvent.class));

            verifyNoMoreInteractions(ticketGetService, ticketUpdateService, applicationEventPublisher);
        }
    }
}
