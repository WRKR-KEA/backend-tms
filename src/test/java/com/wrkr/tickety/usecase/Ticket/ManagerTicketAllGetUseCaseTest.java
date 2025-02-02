package com.wrkr.tickety.usecase.Ticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.domains.ticket.application.dto.response.ManagerTicketAllGetPagingResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketAllGetUseCase;
import com.wrkr.tickety.domains.ticket.domain.constant.SortType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ManagerTicketAllGetUseCaseTest {

    @InjectMocks
    private ManagerTicketAllGetUseCase managerTicketAllGetUseCase;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private TicketGetService ticketGetService;

    @Mock
    private PkCrypto pkCrypto; // Mock 객체 생성

    @BeforeEach
    void setUp() {
        PkCrypto.setInstance(pkCrypto);
    }

    @Test
    @DisplayName("담당자의 티켓 목록을 조회한다.")
    void getManagerTickets() {
        // given
        Member member = Member.builder().memberId(1L).build();
        Member requestMember = Member.builder().memberId(2L).build();
        Long managerId = 1L;
        int page = 0;
        int size = 10;
        String search = "search";
        Pageable pageable = PageRequest.of(page, size);
        String cryptoManagerId = "W1NMMfAHGTnNGLdRL3lvcw";
        List<Ticket> tickets = List.of(
            Ticket.builder().ticketId(1L).isPinned(true).status(TicketStatus.CANCEL).user(requestMember).build(),
            Ticket.builder().ticketId(2L).isPinned(true).status(TicketStatus.COMPLETE).user(requestMember).build(),
            Ticket.builder().ticketId(3L).isPinned(false).status(TicketStatus.IN_PROGRESS).user(requestMember).build()
        );
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Ticket> ticketsPage = new PageImpl<>(tickets, pageRequest, 3);
        when(memberGetService.byMemberId(managerId)).thenReturn(member);
        when(pkCrypto.decryptValue(cryptoManagerId)).thenReturn(managerId);
        given(ticketGetService.getTicketsByManagerFilter(managerId, pageable, null, search, SortType.NEWEST)).willReturn(ticketsPage);

        // when
        ManagerTicketAllGetPagingResponse ticketAllGetPagingResponse = managerTicketAllGetUseCase.getManagerTicketList(managerId, pageable, null, search,
            SortType.NEWEST);

        // then
        assertEquals(3, ticketAllGetPagingResponse.tickets().size());
    }

    @Test
    @DisplayName("담당자의 티켓 목록을 조회한다. 담당가id가 없는 경우를 테스트한다.")
    void throwMemberExceptionTest() {
        // given
        String cryptoManagerId = "W1NMMfAHGTnNGLdRL3lvcw";
        long managerId = pkCrypto.decryptValue(cryptoManagerId);

//        when(memberGetService.byMemberId(managerId)).thenReturn(Optional.empty());

        //then
        ApplicationException exception = assertThrows(ApplicationException.class,
            () -> memberGetService.byMemberId(managerId));

        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND, exception.getCode());
    }


}
