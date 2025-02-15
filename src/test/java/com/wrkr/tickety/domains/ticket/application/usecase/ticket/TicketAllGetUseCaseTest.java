package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class TicketAllGetUseCaseTest {

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    private static final Long USER_ID = 1L;
    private static final Long TICKET_ID = 100L;
    private static final TicketStatus STATUS = TicketStatus.REQUEST;

    private Member user;
    private Ticket ticket;
    private ApplicationPageRequest pageRequest;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime firstManagerChangeDate;
    private Category parentCategory;
    private Category childCategory;

    @Mock
    private TicketGetService ticketGetService;

    @Mock
    private TicketHistoryGetService ticketHistoryGetService;

    @Mock
    private MemberGetService memberGetService;

    @InjectMocks
    private TicketAllGetUseCase ticketAllGetUseCase;

    @BeforeEach
    void setUp() {
        createdAt = LocalDateTime.now().minusDays(1); // 하루 전 생성
        updatedAt = LocalDateTime.now(); // 현재 시간
        firstManagerChangeDate = LocalDateTime.now().minusHours(5); // 5시간 전 변경

        user = Member.builder()
            .memberId(USER_ID)
            .nickname("사용자")
            .email("user@naver.com")
            .isDeleted(false)
            .build();

        parentCategory = Category.builder()
            .categoryId(1L)
            .name("부모 카테고리")
            .build();

        childCategory = Category.builder()
            .categoryId(2L)
            .name("자식 카테고리")
            .parent(parentCategory)
            .build();

        ticket = Ticket.builder()
            .ticketId(TICKET_ID)
            .user(user)
            .category(childCategory)
            .title("테스트 티켓")
            .serialNumber("TCK-123456")
            .status(STATUS)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();

        pageRequest = new ApplicationPageRequest(1, 10, null);
    }

    @Test
    @DisplayName("성공: 사용자의 전체 티켓 조회")
    void getAllTickets_Success() {
        // given
        Page<Ticket> ticketPage = new PageImpl<>(List.of(ticket));

        given(memberGetService.byMemberId(USER_ID)).willReturn(user);
        given(ticketGetService.getTicketsByUserId(USER_ID, pageRequest)).willReturn(ticketPage);
        given(ticketHistoryGetService.getFirstManagerChangeDate(TICKET_ID)).willReturn(firstManagerChangeDate);

        // when
        ApplicationPageResponse<TicketAllGetResponse> response = ticketAllGetUseCase.getAllTickets(USER_ID, pageRequest, null);

        // then
        assertThat(response.elements()).isNotEmpty();
        assertThat(response.elements().get(0).title()).isEqualTo(ticket.getTitle());
        assertThat(response.elements().get(0).status()).isEqualTo(STATUS);
        assertThat(response.elements().get(0).createdAt()).isNotNull();
        assertThat(response.elements().get(0).updatedAt()).isNotNull();
    }

    @Test
    @DisplayName("성공: 특정 상태의 티켓 조회")
    void getAllTickets_WithStatus_Success() {
        // given
        Page<Ticket> ticketPage = new PageImpl<>(List.of(ticket));

        given(memberGetService.byMemberId(USER_ID)).willReturn(user);
        given(ticketGetService.getTicketsByUserIdAndStatus(USER_ID, STATUS, pageRequest)).willReturn(ticketPage);
        given(ticketHistoryGetService.getFirstManagerChangeDate(TICKET_ID)).willReturn(firstManagerChangeDate);

        // when
        ApplicationPageResponse<TicketAllGetResponse> response = ticketAllGetUseCase.getAllTickets(USER_ID, pageRequest, STATUS);

        // then
        assertThat(response.elements()).isNotEmpty();
        assertThat(response.elements().get(0).status()).isEqualTo(STATUS);
    }

    @Test
    @DisplayName("실패: 존재하지 않는 사용자 ID로 조회 시 예외 발생")
    void getAllTickets_UserNotFound() {
        // given
        given(memberGetService.byMemberId(USER_ID))
            .willThrow(new ApplicationException(MemberErrorCode.MEMBER_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> ticketAllGetUseCase.getAllTickets(USER_ID, pageRequest, null))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }
}
