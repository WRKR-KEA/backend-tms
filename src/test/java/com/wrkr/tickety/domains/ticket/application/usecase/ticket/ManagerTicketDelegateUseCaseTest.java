package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_MANAGER_NOT_MATCH;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_DELEGATABLE;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_FOUND;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.wrkr.tickety.common.UnitTest;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.TicketDelegateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ManagerTicketDelegateUseCaseTest extends UnitTest {

    @Spy
    private final ManagerTicketDelegateUseCase sut = new ManagerTicketDelegateUseCase(
        ticketGetService,
        ticketUpdateService,
        ticketHistorySaveService,
        memberGetService,
        applicationEventPublisher
    );


    private static final Long USER_ID = 1L;
    private static final Long MANAGER_ID = 2L;
    private static final Long TICKET_ID = 1L;
    private static final Long TICKET_CATEGORY_ID = 1L;
    private static final String TICKET_SERIAL = "#12345678";
    private static final String TICKET_TITLE = "티켓 제목";
    private static final String TICKET_CONTENT = "티켓 내용";

    private Member user;
    private Category category;
    private Ticket ticket;

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("TEST", "TESTSECRETKEY");
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

        Member manager = Member.builder()
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
        // Given
        Long delegateManagerId = 3L;

        TicketDelegateRequest ticketDelegateRequest = TicketDelegateRequest.builder()
            .delegateManagerId(PkCrypto.encrypt(delegateManagerId))
            .build();

        TicketPkResponse mockResponse = TicketPkResponse.builder()
            .ticketId(PkCrypto.encrypt(TICKET_ID))
            .build();

        doReturn(mockResponse).when(sut).delegateTicket(TICKET_ID, MANAGER_ID, ticketDelegateRequest);

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

        given(memberGetService.byMemberId(delegateManagerId)).willReturn(delegateManager);
        given(ticketGetService.getTicketByTicketId(TICKET_ID)).willReturn(ticket);
        given(ticketUpdateService.updateManager(ticket, delegateManager)).willReturn(updatedTicket);

        // When
        TicketPkResponse response = sut.delegateTicket(TICKET_ID, MANAGER_ID, ticketDelegateRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.ticketId()).isEqualTo(PkCrypto.encrypt(TICKET_ID));
        assertThat(updatedTicket.getManager()).isEqualTo(delegateManager);

        verify(sut).delegateTicket(TICKET_ID, MANAGER_ID, ticketDelegateRequest);
    }

    @Test
    @DisplayName("티켓이 존재하지 않으면 예외 발생 (TICKET_NOT_FOUND)")
    void shouldThrowExceptionWhenTicketNotFound() {
        // Given
        Long delegateManagerId = 3L;
        TicketDelegateRequest request = TicketDelegateRequest.builder()
            .delegateManagerId(PkCrypto.encrypt(delegateManagerId))
            .build();

        doThrow(ApplicationException.from(TICKET_NOT_FOUND)).when(sut).delegateTicket(TICKET_ID, MANAGER_ID, request);

        given(ticketGetService.getTicketByTicketId(TICKET_ID)).willThrow(ApplicationException.from(TICKET_NOT_FOUND));

        // When & Then
        assertThatThrownBy(() -> sut.delegateTicket(TICKET_ID, MANAGER_ID, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TICKET_NOT_FOUND.getMessage());

        verify(sut).delegateTicket(TICKET_ID, MANAGER_ID, request);
    }

    @Test
    @DisplayName("현재 관리자가 아닌 사용자가 변경을 시도하면 예외 발생 (TICKET_MANAGER_NOT_MATCH)")
    void shouldThrowExceptionWhenUserIsNotCurrentManager() {
        // Given
        Long delegateManagerId = 3L;
        TicketDelegateRequest request = TicketDelegateRequest.builder()
            .delegateManagerId(PkCrypto.encrypt(delegateManagerId))
            .build();

        Member otherManager = Member.builder().memberId(999L).build(); // 현재 관리자가 아님
        Ticket otherManagerTicket = Ticket.builder().manager(otherManager).build();

        doThrow(ApplicationException.from(TICKET_MANAGER_NOT_MATCH)).when(sut).delegateTicket(TICKET_ID, MANAGER_ID, request);

        given(ticketGetService.getTicketByTicketId(TICKET_ID)).willReturn(otherManagerTicket);

        // When & Then
        assertThatThrownBy(() -> sut.delegateTicket(TICKET_ID, MANAGER_ID, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TICKET_MANAGER_NOT_MATCH.getMessage());

        verify(sut).delegateTicket(TICKET_ID, MANAGER_ID, request);
    }

    @Test
    @DisplayName("담당자 변경이 불가능한 상태일 때 예외 발생 (TICKET_NOT_DELEGATABLE)")
    void shouldThrowExceptionWhenTicketIsNotDelegatable() {
        // Given
        Long delegateManagerId = 3L;
        TicketDelegateRequest request = TicketDelegateRequest.builder()
            .delegateManagerId(PkCrypto.encrypt(delegateManagerId))
            .build();

        ticket.updateStatus(TicketStatus.COMPLETE); // 이미 완료된 티켓으로 설정 (위임 불가)

        doThrow(ApplicationException.from(TICKET_NOT_DELEGATABLE)).when(sut).delegateTicket(TICKET_ID, MANAGER_ID, request);
        given(ticketGetService.getTicketByTicketId(TICKET_ID)).willReturn(ticket);

        // When & Then
        assertThatThrownBy(() -> sut.delegateTicket(TICKET_ID, MANAGER_ID, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TICKET_NOT_DELEGATABLE.getMessage());

        verify(sut).delegateTicket(TICKET_ID, MANAGER_ID, request);
    }

    @Test
    @DisplayName("존재하지 않는 담당자로 변경을 시도하면 예외 발생 (MEMBER_NOT_FOUND)")
    void shouldThrowExceptionWhenNewManagerNotFound() {
        // Given
        Long delegateManagerId = 3L;
        TicketDelegateRequest request = TicketDelegateRequest.builder()
            .delegateManagerId(PkCrypto.encrypt(delegateManagerId))
            .build();

        doThrow(ApplicationException.from(MEMBER_NOT_FOUND)).when(sut).delegateTicket(TICKET_ID, MANAGER_ID, request);

        given(ticketGetService.getTicketByTicketId(TICKET_ID)).willReturn(ticket);
        given(memberGetService.byMemberId(delegateManagerId)).willThrow(ApplicationException.from(MEMBER_NOT_FOUND));

        // When & Then
        assertThatThrownBy(() -> sut.delegateTicket(TICKET_ID, MANAGER_ID, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(MEMBER_NOT_FOUND.getMessage());

        verify(sut).delegateTicket(TICKET_ID, MANAGER_ID, request);
    }
}
