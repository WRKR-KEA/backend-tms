package com.wrkr.tickety.usecase;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.ticket.application.dto.request.TicketDelegateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketDelegateUseCase;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketUpdateService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistorySaveService;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ManagerTicketUseCaseTest {

    @Mock
    private TicketGetService ticketGetService;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private TicketUpdateService ticketUpdateService;

    @Mock
    private TicketHistorySaveService ticketHistorySaveService;

    @InjectMocks
    private ManagerTicketDelegateUseCase managerTicketDelegateUseCase;

    private static final Long USER_ID = 1L;
    private static final Long MANAGER_ID = 2L;
    private static final Long TICKET_ID = 1L;
    private static final Long TICKET_CATEGORY_ID = 1L;
    private static final String TICKET_SERIAL = "#12345678";
    private static final String TICKET_TITLE = "티켓 제목";
    private static final String TICKET_CONTENT = "티켓 내용";

    private Member user;
    private Member manager;
    private Category category;
    private Ticket ticket;

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @BeforeEach
    void setUp() {
        user = Member.builder()
            .memberId(USER_ID)
            .password("password")
            .nickname("사용자")
            .email("email@naver.com")
            .phone("010-1234-5678")
            .name("사용자")
            .role(Role.USER)
            .build();

        manager = Member.builder()
            .memberId(MANAGER_ID)
            .password("password")
            .nickname("담당자")
            .email("email@naver.com")
            .phone("010-1234-5678")
            .name("담당자")
            .role(Role.MANAGER)
            .build();

        category = Category.builder()
            .categoryId(TICKET_CATEGORY_ID)
            .name("카테고리")
            .seq(1)
            .build();

        ticket = Ticket.builder()
            .ticketId(TICKET_ID)
            .manager(manager)
            .user(user)
            .title(TICKET_TITLE)
            .content(TICKET_CONTENT)
            .serialNumber(TICKET_SERIAL)
            .status(TicketStatus.IN_PROGRESS)
            .category(category)
            .build();
    }

    @Test
    @DisplayName("담당자 본인이 승인 했던 티켓을 다른 담당자에게 배정한다 ")
    void testDelegateTicket() {
        // given
        Long delegateManagerId = 3L;

        TicketDelegateRequest ticketDelegateRequest = TicketDelegateRequest.builder()
            .currentManagerId(PkCrypto.encrypt(MANAGER_ID))
            .delegateManagerId(PkCrypto.encrypt(delegateManagerId))
            .build();

        Member delegateManager = Member.builder()
            .memberId(delegateManagerId)
            .password("password")
            .nickname("담당자")
            .phone("010-1234-5678")
            .email("email")
            .name("담당자")
            .role(Role.MANAGER)
            .build();

        Ticket updatedTicket = Ticket.builder()
            .ticketId(TICKET_ID)
            .user(user)
            .manager(delegateManager)
            .title(TICKET_TITLE)
            .content(TICKET_CONTENT)
            .serialNumber(TICKET_SERIAL)
            .status(TicketStatus.IN_PROGRESS)
            .category(category)
            .build();

        given(memberGetService.byMemberId(MANAGER_ID)).willReturn(Optional.of(manager));
        given(memberGetService.byMemberId(delegateManagerId)).willReturn(
            Optional.of(delegateManager));
        given(ticketGetService.getTicketByTicketId(TICKET_ID)).willReturn(ticket);
        given(ticketUpdateService.updateManager(ticket, delegateManager)).willReturn(updatedTicket);

        // when
        PkResponse response = managerTicketDelegateUseCase.delegateTicket(TICKET_ID, MANAGER_ID,
            ticketDelegateRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(PkCrypto.encrypt(TICKET_ID));
        assertThat(updatedTicket.getManager()).isEqualTo(delegateManager);

        verify(memberGetService).byMemberId(MANAGER_ID);
        verify(memberGetService).byMemberId(delegateManagerId);
        verify(ticketGetService).getTicketByTicketId(TICKET_ID);
        verify(ticketUpdateService).updateManager(ticket, delegateManager);

    }

}
