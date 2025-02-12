package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketDetailGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketSaveService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketUpdateService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistorySaveService;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
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
public class TicketUseCaseTest {

    @Mock
    private TicketSaveService ticketSaveService;

    @Mock
    private TicketGetService ticketGetService;

    @Mock
    private CategoryGetService categoryGetService;

    @Mock
    private TicketUpdateService ticketUpdateService;

    @Mock
    private TicketHistoryGetService ticketHistoryGetService;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private TicketHistorySaveService ticketHistorySaveService;

    @InjectMocks
    private TicketCreateUseCase ticketCreateUseCase;

    @InjectMocks
    private TicketAllGetUseCase ticketAllGetUseCase;

    @InjectMocks
    private TicketDetailGetUseCase ticketDetailGetUseCase;

    @InjectMocks
    private TicketCancelUseCase ticketCancelUseCase;

    private Member user;
    private Category parentCategory;
    private Category childCategory;
    private Ticket ticket;

    private static final Long USER_ID = 1L;
    private static final Long TICKET_ID = 1L;
    private static final Long TICKET_CATEGORY_ID = 1L;
    private static final String TICKET_SERIAL = "#12345678";
    private static final String TICKET_TITLE = "티켓 제목";
    private static final String TICKET_CONTENT = "티켓 내용";

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

        parentCategory = Category.builder()
            .categoryId(2L)
            .name("카테고리")
            .seq(1)
            .build();

        childCategory = Category.builder()
            .categoryId(TICKET_CATEGORY_ID)
            .parent(parentCategory)
            .name("카테고리")
            .seq(1)
            .build();

        ticket = Ticket.builder()
            .ticketId(TICKET_ID)
            .user(user)
            .title(TICKET_TITLE)
            .content(TICKET_CONTENT)
            .serialNumber(TICKET_SERIAL)
            .status(TicketStatus.REQUEST)
            .category(childCategory)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("사용자가 요청한 티켓이 저장되면 저장된 티켓 정보를 검증한다")
    void createTicket() {
        // Given
        TicketCreateRequest ticketCreateRequest = TicketCreateRequest.builder()
            .title("티켓 제목")
            .content("티켓 내용")
            .categoryId(PkCrypto.encrypt(TICKET_CATEGORY_ID))
            .build();

        given(categoryGetService.getChildrenCategory(anyLong())).willReturn(childCategory);

        given(memberGetService.byMemberId(anyLong())).willReturn(user);
        given(ticketSaveService.save(any(Ticket.class))).willReturn(ticket);

        // When
        TicketPkResponse pkResponse = ticketCreateUseCase.createTicket(ticketCreateRequest, USER_ID);

        // Then
        assertThat(pkResponse).isNotNull();
        assertThat(pkResponse.ticketId()).isEqualTo(PkCrypto.encrypt(1L));

        verify(categoryGetService).getChildrenCategory(anyLong());
        verify(memberGetService).byMemberId(anyLong());
        verify(ticketSaveService).save(any(Ticket.class));
    }

    @Test
    @DisplayName("사용자가 요청했던 티켓들에 페이지네이션을 적용해서 전체 조회한다")
    void getTickets() {
        // Given
        ApplicationPageRequest pageRequest = new ApplicationPageRequest(1, 10, null);

        Page<Ticket> ticketPage = new PageImpl<>(List.of(ticket));

        given(ticketGetService.getTicketsByUserIdAndStatus(anyLong(), any(TicketStatus.class), any(ApplicationPageRequest.class)))
            .willReturn(ticketPage);
        given(memberGetService.byMemberId(anyLong())).willReturn(user);
        given(ticketHistoryGetService.getFirstManagerChangeDate(anyLong())).willReturn(LocalDateTime.now());

        // When
        ApplicationPageResponse<TicketAllGetResponse> ticketAllGetPagingResponse = ticketAllGetUseCase.getAllTickets(USER_ID, pageRequest,
                                                                                                                     TicketStatus.REQUEST);

        // Then
        assertThat(ticketAllGetPagingResponse).isNotNull();
        assertThat(ticketAllGetPagingResponse.currentPage()).isEqualTo(1);
        assertThat(ticketAllGetPagingResponse.size()).isEqualTo(1);
        assertThat(ticketAllGetPagingResponse.elements().getFirst().serialNumber()).isEqualTo("#12345678");

        verify(ticketGetService).getTicketsByUserIdAndStatus(anyLong(), any(TicketStatus.class), any(ApplicationPageRequest.class));
        verify(memberGetService).byMemberId(anyLong());
    }

    @Test
    @DisplayName("사용자가 요청한 특정 티켓을 조회한다")
    void getTicket() {
        // Given
        given(ticketGetService.getTicketByTicketId(anyLong())).willReturn(ticket);
        given(ticketHistoryGetService.getFirstManagerChangeDate(anyLong())).willReturn(LocalDateTime.now());

        // When
        TicketDetailGetResponse response = ticketDetailGetUseCase.getTicket(USER_ID, TICKET_ID);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(PkCrypto.encrypt(1L));
        assertThat(response.title()).isEqualTo("티켓 제목");
        assertThat(response.content()).isEqualTo("티켓 내용");

        verify(ticketGetService).getTicketByTicketId(anyLong());
        verify(ticketHistoryGetService).getFirstManagerChangeDate(anyLong());
    }

    @Test
    @DisplayName("사용자가 요청했던 특정 티켓을 취소한다")
    void cancelTicket() {
        // Given
        Ticket updatedTicket = Ticket.builder()
            .ticketId(TICKET_ID)
            .category(childCategory)
            .content("티켓 내용")
            .serialNumber("#12345678")
            .title("티켓 제목")
            .status(TicketStatus.CANCEL)
            .user(user)
            .build();

        given(ticketGetService.getTicketByTicketId(anyLong())).willReturn(ticket);
        given(ticketUpdateService.updateStatus(any(Ticket.class), any(TicketStatus.class))).willReturn(updatedTicket);

        // When
        TicketPkResponse pkResponse = ticketCancelUseCase.cancelTicket(USER_ID, TICKET_ID);

        // Then
        assertThat(pkResponse).isNotNull();
        assertThat(pkResponse.ticketId()).isEqualTo(PkCrypto.encrypt(1L));

        verify(ticketGetService).getTicketByTicketId(anyLong());
        verify(ticketUpdateService).updateStatus(any(Ticket.class), any(TicketStatus.class));
    }
}

