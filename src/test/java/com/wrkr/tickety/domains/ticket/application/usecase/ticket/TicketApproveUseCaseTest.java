package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.wrkr.tickety.common.fixture.member.UserFixture;
import com.wrkr.tickety.common.fixture.ticket.TicketFixture;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketUpdateService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistorySaveService;
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
    }
}