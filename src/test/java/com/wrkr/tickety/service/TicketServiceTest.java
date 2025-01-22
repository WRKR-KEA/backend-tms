package com.wrkr.tickety.service;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketSaveService;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TicketPersistenceAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketPersistenceAdapter ticketPersistenceAdapter;

    @InjectMocks
    private TicketSaveService ticketSaveService;

    private Member user;
    private Member manager;

    @BeforeEach
    void setUp() {
        user = Member.builder()
                .nickname("testUser")
                .password("password123")
                .name("Test User")
                .phone("010-1234-5678")
                .email("test@example.com")
                .position("Developer")
                .profileImage("profile.jpg")
                .role(Role.USER)
                .build();

        manager = Member.builder()
                .nickname("manager")
                .password("password456")
                .name("Manager User")
                .phone("010-9876-5432")
                .email("manager@example.com")
                .position("Manager")
                .profileImage("manager.jpg")
                .role(Role.ADMIN)
                .build();
    }

    @Test
    @DisplayName("사용자가 요청한 티켓이 저장되면 저장된 티켓 정보를 검증한다")
    void saveTicket() {
        // given
        Category category = Category.builder()
                .name("VM")
                .seq(1)
                .build();

        Ticket ticket = Ticket.builder()
                .user(user)
                .manager(manager)
                .category(category)
                .status(TicketStatus.REQUEST)
                .serialNumber("12345678")
                .title("티켓 제목")
                .content("티켓 내용")
                .build();

        when(ticketPersistenceAdapter.save(ticket)).thenReturn(ticket);

        // when
        Ticket savedTicket = ticketSaveService.save(ticket);

        // then
        assertThat(savedTicket).isNotNull();
        assertThat(savedTicket.getSerialNumber()).isEqualTo("12345678");
        assertThat(savedTicket.getTitle()).isEqualTo("티켓 제목");
        verify(ticketPersistenceAdapter, times(1)).save(ticket);
    }
}
