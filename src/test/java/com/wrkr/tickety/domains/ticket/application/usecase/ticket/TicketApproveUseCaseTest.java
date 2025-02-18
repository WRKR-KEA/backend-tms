package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static com.wrkr.tickety.common.fixture.member.UserFixture.MANAGER_B;
import static com.wrkr.tickety.common.fixture.ticket.TicketFixture.TICKET_IN_PROGRESS_01;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wrkr.tickety.common.fixture.member.UserFixture;
import com.wrkr.tickety.common.fixture.ticket.TicketFixture;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.event.RecordApplicationEvents;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RecordApplicationEvents
class TicketApproveUseCaseTest {

    @InjectMocks
    private TicketApproveUseCase ticketApproveUseCase;

    @Mock
    private TicketGetService ticketGetService;

    @Mock
    private TicketUpdateService ticketUpdateService;

    @Mock
    private TicketHistorySaveService ticketHistorySaveService;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private PkCrypto pkCrypto;

    @BeforeEach
    public void setUp() {
        pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Nested
    @DisplayName("TicketApprove UseCase Layer > 담당자 티켓 승인")
    class approveTicket {

        @Test
        @DisplayName("1개의 티켓에 대해 5개의 스레드가 동시에 승인 시 1개의 스레드만 승인에 성공한다.")
        void handleTicketApprovalConcurrency() throws Exception {
            // Given
            Member member = UserFixture.MANAGER_C.toMember();
            Ticket requestedTicket = TicketFixture.TICKET_REQUEST_01.toRequestTicket();

            // When
            when(ticketGetService.getTicketByTicketId(requestedTicket.getTicketId())).thenReturn(requestedTicket);
            when(memberGetService.byMemberId(member.getMemberId())).thenReturn(member);

            Mockito.lenient().when(ticketUpdateService.approveTicket(Mockito.any(Ticket.class), Mockito.any(Member.class)))
                .thenAnswer(invocation -> {
                    synchronized (this) {
                        Ticket t = invocation.getArgument(0);
                        if (t.getStatus() == TicketStatus.REQUEST) {
                            t.updateStatus(TicketStatus.IN_PROGRESS);
                            return t;
                        } else {
                            throw new IllegalStateException("Ticket already approved.");
                        }
                    }
                });

            AtomicInteger failCount = new AtomicInteger(0);
            ExecutorService executorService = Executors.newFixedThreadPool(5);

            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch endLatch = new CountDownLatch(5);

            Runnable task = () -> {
                try {
                    startLatch.await();
                    ticketApproveUseCase.approveTicket(
                        member.getMemberId(),
                        List.of(PkCrypto.encrypt(requestedTicket.getTicketId()))
                    );
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    endLatch.countDown();
                }
            };

            for (int i = 0; i < 5; i++) {
                executorService.submit(task);
            }

            startLatch.countDown();
            endLatch.await();
            executorService.shutdown();

            // Then
            assertEquals(4, failCount.get());
            assertEquals(requestedTicket.getStatus(), TicketStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("요청 상태인 티켓이 아니라면 TICKET_NOT_APPROVABLE 예외가 발생한다.")
        void throwExceptionByTicketNotApprovable() {
            // given
            Ticket ticket = TICKET_IN_PROGRESS_01.toInProgressTicket();
            Member manager = MANAGER_B.toMember();

            given(memberGetService.byMemberId(manager.getMemberId())).willReturn(manager);
            given(ticketGetService.getTicketByTicketId(ticket.getTicketId())).willReturn(ticket);

            // when - then
            assertThatThrownBy(() -> ticketApproveUseCase.approveTicket(manager.getMemberId(), List.of(PkCrypto.encrypt(ticket.getTicketId()))))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(TicketErrorCode.TICKET_NOT_APPROVABLE.getMessage());
        }

        @Test
        @DisplayName("승인하려는 회원이 담당자 권한이 아니라면 MEMBER_NOT_ALLOWED 예외가 발생한다.")
        void throwExceptionByMemberNotAllowed() {
            // given
            Ticket ticket = TicketFixture.TICKET_REQUEST_01.toRequestTicket();
            Member user = UserFixture.USER_A.toMember();
            given(ticketGetService.getTicketByTicketId(ticket.getTicketId())).willReturn(ticket);

            given(memberGetService.byMemberId(user.getMemberId())).willReturn(user);
            given(ticketGetService.getTicketByTicketId(ticket.getTicketId())).willReturn(ticket);

            // when - then
            assertThatThrownBy(() -> ticketApproveUseCase.approveTicket(user.getMemberId(), List.of(PkCrypto.encrypt(ticket.getTicketId()))))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_ALLOWED.getMessage());
        }

        @Test
        @DisplayName("담당자는 요청된 티켓 승인 처리에 성공한다.")
        void successApproveTicket() {
            // Given
            Member member = UserFixture.MANAGER_C.toMember();
            Ticket ticket = TicketFixture.TICKET_REQUEST_01.toRequestTicket();

            given(memberGetService.byMemberId(member.getMemberId())).willReturn(member);
            given(ticketGetService.getTicketByTicketId(ticket.getTicketId())).willReturn(ticket);
            given(ticketUpdateService.approveTicket(ticket, member)).willReturn(ticket);
            willDoNothing().given(applicationEventPublisher).publishEvent(ArgumentMatchers.any(TicketStatusChangeEvent.class));

            // When
            List<TicketPkResponse> response = ticketApproveUseCase.approveTicket(member.getMemberId(), List.of(PkCrypto.encrypt(ticket.getTicketId())));

            // then
            assertFalse(response.isEmpty());

            verify(ticketHistorySaveService).save(ArgumentMatchers.any(TicketHistory.class));
            verify(applicationEventPublisher, times(1)).publishEvent(ArgumentMatchers.any(TicketStatusChangeEvent.class));
        }
    }
}