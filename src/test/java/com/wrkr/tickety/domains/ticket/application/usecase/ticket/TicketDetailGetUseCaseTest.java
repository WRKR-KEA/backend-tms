package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketDetailGetResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TicketDetailGetUseCaseTest {

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final Long TICKET_ID = 100L;

    private Member user;
    private Member otherUser;
    private Ticket ticket;
    private Category category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime firstManagerChangeDate;
    private LocalDateTime completeDate;

    @Mock
    private TicketGetService ticketGetService;

    @Mock
    private TicketHistoryGetService ticketHistoryGetService;

    @InjectMocks
    private TicketDetailGetUseCase ticketDetailGetUseCase;

    @BeforeEach
    void setUp() {
        createdAt = LocalDateTime.now().minusDays(1); // 하루 전 생성
        updatedAt = LocalDateTime.now(); // 현재 시간
        firstManagerChangeDate = LocalDateTime.now().minusHours(5); // 5시간 전 변경
        completeDate = LocalDateTime.now().minusHours(1); // 1시간 전 완료

        user = Member.builder()
            .memberId(USER_ID)
            .nickname("사용자")
            .email("user@naver.com")
            .isDeleted(false)
            .build();

        Category parentCategory = Category.builder()
            .categoryId(10L)
            .name("부모 카테고리")
            .build();

        category = Category.builder()
            .categoryId(1L)
            .name("테스트 카테고리")
            .parent(parentCategory)  // 부모 카테고리 설정
            .build();

        ticket = Ticket.builder()
            .ticketId(TICKET_ID)
            .user(user)
            .title("테스트 티켓")
            .content("티켓 상세 테스트")
            .serialNumber("TCK-123456")
            .status(TicketStatus.REQUEST)
            .category(category)  // 부모 카테고리 포함된 category 사용
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }


    @Test
    @DisplayName("성공: 사용자의 특정 티켓 조회")
    void getTicket_Success() {
        // given
        given(ticketGetService.getTicketByTicketId(TICKET_ID)).willReturn(ticket);
        given(ticketHistoryGetService.getFirstManagerChangeDate(TICKET_ID)).willReturn(firstManagerChangeDate);
        given(ticketHistoryGetService.getCompleteDate(ticket)).willReturn(completeDate);

        // when
        TicketDetailGetResponse response = ticketDetailGetUseCase.getTicket(USER_ID, TICKET_ID);

        // then
        assertThat(response.id()).isNotNull();
        assertThat(response.title()).isEqualTo(ticket.getTitle());
        assertThat(response.content()).isEqualTo(ticket.getContent());
        assertThat(response.status()).isEqualTo(ticket.getStatus());
        assertThat(response.userNickname()).isEqualTo(user.getNickname());
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.updatedAt()).isNotNull();
        assertThat(response.startedAt()).isNotNull();
        assertThat(response.completedAt()).isNotNull();
    }

    @Test
    @DisplayName("실패: 사용자가 자신의 티켓이 아닌 경우 예외 발생")
    void getTicket_UnauthorizedAccess() {
        // given
        given(ticketGetService.getTicketByTicketId(TICKET_ID)).willReturn(ticket);

        // when & then
        assertThatThrownBy(() -> ticketDetailGetUseCase.getTicket(OTHER_USER_ID, TICKET_ID))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TicketErrorCode.UNAUTHORIZED_ACCESS.getMessage());
    }

    @Test
    @DisplayName("실패: 존재하지 않는 티켓 ID 조회 시 예외 발생")
    void getTicket_TicketNotFound() {
        // given
        given(ticketGetService.getTicketByTicketId(TICKET_ID))
            .willThrow(new ApplicationException(TicketErrorCode.TICKET_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> ticketDetailGetUseCase.getTicket(USER_ID, TICKET_ID))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TicketErrorCode.TICKET_NOT_FOUND.getMessage());
    }
}
