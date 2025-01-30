package com.wrkr.tickety.service.ticket;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketPersistenceAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
        Random random = new Random();

        for (long i = 1; i <= 10; i++) {
            Ticket ticket = Ticket.builder()
                .ticketId(i)
                .user(Member.builder().memberId(i + 1).build())
                .manager(Member.builder().memberId(1L).build())
                .category(Category.builder().categoryId(1L).name("test").build())
                .serialNumber("SN-" + i)
                .title("Test Ticket " + i)
                .content("This is test ticket number " + i)
                .status(random.nextBoolean() ? TicketStatus.IN_PROGRESS : null)
                .isPinned(random.nextBoolean())
                .build();

            tickets.add(ticket);
        }
        return tickets;
    }

    @Test
    void getTicketByManagerFilter() {
        //given
        List<Ticket> tickets = createTestTickets();
        Long managerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        String query = "Test Ticket";

        Page<Ticket> ticketPage = new PageImpl<>(tickets, pageable, tickets.size());

        //when
        when(ticketPersistenceAdapter.findAllByManagerFilter(managerId, pageable, null, query)).thenReturn(ticketPage);

        //then
        Page<Ticket> result = ticketGetService.getTicketsByManagerFilter(managerId, pageable, null, query);
        assertThat(result).isEqualTo(ticketPage);
    }
}
