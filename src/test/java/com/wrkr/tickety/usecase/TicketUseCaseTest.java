package com.wrkr.tickety.usecase;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.ticket.application.dto.request.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketAllGetPagingResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketDetailGetResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.TicketAllGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.TicketCancelUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.TicketCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.TicketDetailGetUseCase;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketSaveService;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketUpdateService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.tickethistory.TicketHistorySaveService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    private TicketHistorySaveService ticketHistorySaveService;

    @Mock
    private TicketHistoryGetService ticketHistoryGetService;

    @Mock
    private MemberGetService memberGetService;

    @InjectMocks
    private TicketCreateUseCase ticketCreateUseCase;

    @InjectMocks
    private TicketAllGetUseCase ticketAllGetUseCase;

    @InjectMocks
    private TicketDetailGetUseCase ticketDetailGetUseCase;

    @InjectMocks
    private TicketCancelUseCase ticketCancelUseCase;

    private Member user;
    private Category category;

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @BeforeEach
    void setUp() {
        user = Member.builder()
            .memberId(1L)
            .password("password")
            .nickname("사용자")
            .email("email@naver.com")
            .phone("010-1234-5678")
            .name("사용자")
            .role(Role.USER)
            .build();

        category = Category.builder()
            .categoryId(1L)
            .name("카테고리")
            .seq(1)
            .build();

    }

    @Test
    @DisplayName("사용자가 요청한 티켓이 저장되면 저장된 티켓 정보를 검증한다")
    void createTicket() {
        // given
        Long userId = 1L;
        TicketCreateRequest ticketCreateRequest = TicketCreateRequest.builder()
            .title("티켓 제목")
            .content("티켓 내용")
            .categoryId(PkCrypto.encrypt(1L))
            .build();

        Ticket ticket = Ticket.builder()
            .ticketId(1L)
            .user(user)
            .title(ticketCreateRequest.title())
            .content(ticketCreateRequest.content())
            .serialNumber("#12345678")
            .status(TicketStatus.REQUEST)
            .category(category)
            .build();

        given(categoryGetService.getCategory(anyLong())).willReturn(category);
        given(memberGetService.getUserById(anyLong())).willReturn(java.util.Optional.of(user));
        given(ticketSaveService.save(any(Ticket.class))).willReturn(ticket);

        // when
        PkResponse pkResponse = ticketCreateUseCase.createTicket(ticketCreateRequest, userId);
        // then
        assertThat(pkResponse).isNotNull();
        assertThat(pkResponse.id()).isEqualTo(PkCrypto.encrypt(1L));

        verify(categoryGetService).getCategory(anyLong());
        verify(memberGetService).getUserById(anyLong());
        verify(ticketSaveService).save(any(Ticket.class));
    }

    @Test
    @DisplayName("사용자가 요청했던 티켓들에 페이지네이션을 적용해서 전체 조회한다")
    void getTickets() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Ticket ticket = Ticket.builder()
            .ticketId(1L)
            .user(user)
            .title("티켓 제목")
            .content("티켓 내용")
            .serialNumber("#12345678")
            .status(TicketStatus.REQUEST)
            .category(category)
            .build();

        Page<Ticket> ticketPage = new PageImpl<>(List.of(ticket));
        given(ticketGetService.getTicketsByUserId(anyLong(), any(Pageable.class))).willReturn(
            ticketPage);
        given(memberGetService.getUserById(anyLong())).willReturn(java.util.Optional.of(user));
        given(ticketHistoryGetService.getFirstManagerChangeDate(anyLong())).willReturn(
            LocalDateTime.now());

        //when
        TicketAllGetPagingResponse ticketAllGetPagingResponse = ticketAllGetUseCase.getAllTickets(
            userId, pageable);

        //then
        assertThat(ticketAllGetPagingResponse).isNotNull();
        assertThat(ticketAllGetPagingResponse.currentPage()).isEqualTo(1);
        assertThat(ticketAllGetPagingResponse.size()).isEqualTo(1);
        assertThat(ticketAllGetPagingResponse.tickets().get(0).serialNumber()).isEqualTo(
            "#12345678");

        verify(ticketGetService).getTicketsByUserId(anyLong(), any(Pageable.class));
        verify(memberGetService).getUserById(anyLong());
    }

    @Test
    @DisplayName("사용자가 요청한 특정 티켓을 조회한다")
    void getTicket() {
        // given
        Long userId = 1L;
        Long ticketId = 1L;

        Ticket ticket = Ticket.builder()
            .ticketId(ticketId)
            .category(category)
            .content("티켓 내용")
            .serialNumber("#12345678")
            .title("티켓 제목")
            .status(TicketStatus.REQUEST)
            .user(user)
            .build();

        given(ticketGetService.getTicketByTicketId(anyLong())).willReturn(ticket);
        given(ticketHistoryGetService.getFirstManagerChangeDate(anyLong())).willReturn(
            LocalDateTime.now());

        // when
        TicketDetailGetResponse response = ticketDetailGetUseCase.getTicket(userId, ticketId);

        // then
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
        // given
        Long userId = 1L;
        Long ticketId = 1L;

        Ticket ticket = Ticket.builder()
            .ticketId(ticketId)
            .category(category)
            .content("티켓 내용")
            .serialNumber("#12345678")
            .title("티켓 제목")
            .status(TicketStatus.REQUEST)
            .user(user)
            .build();

        Ticket updatedTicket = Ticket.builder()
            .ticketId(ticketId)
            .category(category)
            .content("티켓 내용")
            .serialNumber("#12345678")
            .title("티켓 제목")
            .status(TicketStatus.CANCEL)
            .user(user)
            .build();

        given(ticketGetService.getTicketByTicketId(anyLong())).willReturn(ticket);
        given(ticketUpdateService.updateStatus(any(Ticket.class),
            any(TicketStatus.class))).willReturn(
            updatedTicket);

        // when
        PkResponse pkResponse = ticketCancelUseCase.cancelTicket(userId, ticketId);

        // then
        assertThat(pkResponse).isNotNull();
        assertThat(pkResponse.id()).isEqualTo(PkCrypto.encrypt(1L));

        verify(ticketGetService).getTicketByTicketId(anyLong());
        verify(ticketUpdateService).updateStatus(any(Ticket.class), any(TicketStatus.class));
    }
}

