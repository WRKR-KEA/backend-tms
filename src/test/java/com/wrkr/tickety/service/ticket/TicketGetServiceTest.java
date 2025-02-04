package com.wrkr.tickety.service.ticket;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.domain.constant.SortType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketPersistenceAdapter;
import com.wrkr.tickety.global.common.dto.PageRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
public class TicketGetServiceTest {

    @Mock
    TicketPersistenceAdapter ticketPersistenceAdapter;

    @InjectMocks
    TicketGetService ticketGetService;

    /**
     * 티켓 리스트 테스트용 데이터 생성
     */
    public List<Ticket> createTestTickets() {
        List<Ticket> tickets = new ArrayList<>();

        for (long i = 1; i <= 10; i++) {
            Ticket ticket = Ticket.builder()
                .ticketId(i)
                .user(Member.builder().memberId(i + 1).build())
                .manager(Member.builder().memberId(1L).build())
                .category(Category.builder().categoryId(1L).name("test").build())
                .serialNumber("SN-" + i)
                .title("Test Ticket " + i)
                .content("This is test ticket number " + i)
                .status(i % 2 == 0 ? TicketStatus.IN_PROGRESS : null)
                .isPinned(i % 2 == 0)
                .build();

            tickets.add(ticket);
        }
        return tickets;
    }

    @Test
    @DisplayName("담당자가 설정한 필터로 담당자의 티켓 티켓 리스트를 조회한다.")
    void getTicketByManagerFilter() {
        //given
        List<Ticket> tickets = createTestTickets();
        Long managerId = 1L;
        PageRequest pageRequest = new PageRequest(0, 10, SortType.NEWEST);
        String query = "Test Ticket";

        Page<Ticket> ticketPage = new PageImpl<>(tickets, pageRequest.toPageable(), tickets.size());

        //when
        when(ticketPersistenceAdapter.findAllByManagerFilter(managerId, pageRequest, null, query)).thenReturn(ticketPage);

        //then
        Page<Ticket> result = ticketGetService.getTicketsByManagerFilter(managerId, pageRequest, null, query);
        assertThat(result.getContent()).isEqualTo(ticketPage.getContent());
        assertThat(result.getTotalElements()).isEqualTo(ticketPage.getTotalElements());
        assertThat(result.getTotalPages()).isEqualTo(ticketPage.getTotalPages());
        assertThat(result.getPageable()).isEqualTo(ticketPage.getPageable());
    }

    @Test
    @DisplayName("담당자가 설정한 필터로 담당자의 티켓 티켓 리스트를 조회한다.정보가 없는 경우를 테스트한다.")
    void getTicketByManagerFilterWithoutContent() {
        //given
        List<Ticket> tickets = new ArrayList<>();
        Long managerId = 1L;
        PageRequest pageRequest = new PageRequest(0, 10, SortType.NEWEST);
        String query = "Test Ticket";

        Page<Ticket> ticketPage = new PageImpl<>(tickets, pageRequest.toPageable(), 0);

        //when
        when(ticketPersistenceAdapter.findAllByManagerFilter(managerId, pageRequest, null, query)).thenReturn(ticketPage);

        //then
        Page<Ticket> result = ticketGetService.getTicketsByManagerFilter(managerId, pageRequest, null, query);
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("담당자가 설정한 필터로 담당자의 티켓 티켓 리스트를 조회한다.페이지가 여러개일 경우를 테스트한다.")
    void getTicketByManagerFilterWithMultiplePages() {
        //given
        List<Ticket> tickets = createTestTickets();
        Long managerId = 1L;
        PageRequest pageRequest = new PageRequest(0, 10, SortType.NEWEST);
        String query = "Test Ticket";

        Page<Ticket> ticketPage = new PageImpl<>(tickets, pageRequest.toPageable(), tickets.size());

        //when
        when(ticketPersistenceAdapter.findAllByManagerFilter(managerId, pageRequest, null, query)).thenReturn(ticketPage);

        //then
        Page<Ticket> result = ticketGetService.getTicketsByManagerFilter(managerId, pageRequest, null, query);
        assertThat(result.getContent()).isEqualTo(ticketPage.getContent());
        assertThat(result.getTotalElements()).isEqualTo(ticketPage.getTotalElements());
        assertThat(result.getTotalPages()).isEqualTo(ticketPage.getTotalPages());
        assertThat(result.getPageable()).isEqualTo(ticketPage.getPageable());
    }
}
