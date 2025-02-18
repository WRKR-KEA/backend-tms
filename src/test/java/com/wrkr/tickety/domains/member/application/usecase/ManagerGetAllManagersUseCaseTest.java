package com.wrkr.tickety.domains.member.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.given;

import com.wrkr.tickety.domains.member.application.dto.response.ManagerGetAllManagerResponse;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManagerGetAllManagersUseCaseTest {

    private static final Long PRINCIPAL_ID = 1L;
    private static final Long MANAGER_1_ID = 2L;
    private static final Long MANAGER_2_ID = 3L;

    private Member principal;
    private Member manager1;
    private Member manager2;
    private Ticket ticket1;
    private Ticket ticket2;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private TicketGetService ticketGetService;

    @InjectMocks
    private ManagerGetAllManagersUseCase managerGetAllManagersUseCase;

    @BeforeEach
    void setUp() {
        principal = Member.builder()
            .memberId(PRINCIPAL_ID)
            .email("principal@company.com")
            .nickname("principal")
            .profileImage("profile1.jpg")
            .position("팀장")
            .phone("010-1111-1111")
            .role(Role.MANAGER)
            .build();

        manager1 = Member.builder()
            .memberId(MANAGER_1_ID)
            .email("manager1@company.com")
            .nickname("manager1")
            .profileImage("profile2.jpg")
            .position("팀원")
            .phone("010-2222-2222")
            .role(Role.MANAGER)
            .build();

        manager2 = Member.builder()
            .memberId(MANAGER_2_ID)
            .email("manager2@company.com")
            .nickname("manager2")
            .profileImage("profile3.jpg")
            .position("팀원")
            .phone("010-3333-3333")
            .role(Role.MANAGER)
            .build();

        ticket1 = Ticket.builder()
            .ticketId(100L)
            .manager(manager1)
            .build();

        ticket2 = Ticket.builder()
            .ticketId(101L)
            .manager(manager2)
            .build();
    }

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Test
    @DisplayName("성공: 모든 매니저와 진행 중인 티켓 개수 반환")
    void getAllManagers_Success() {
        // Given
        List<Member> managers = List.of(principal, manager1, manager2);
        List<Ticket> inProgressTickets = List.of(ticket1, ticket2);
        Map<Long, Long> inProgressTicketCount = Map.of(
            MANAGER_1_ID, 1L,
            MANAGER_2_ID, 1L
        );

        given(memberGetService.getAllManagers()).willReturn(managers);
        given(ticketGetService.getManagersInProgressTickets(anyList())).willReturn(inProgressTickets);

        // When
        ManagerGetAllManagerResponse response = managerGetAllManagersUseCase.getAllManagers(principal);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.principal().memberId()).isEqualTo(PkCrypto.encrypt(PRINCIPAL_ID));
        assertThat(response.managers()).hasSize(2);
        assertThat(response.managers().get(0).ticketAmount()).isEqualTo(1L);
        assertThat(response.managers().get(1).ticketAmount()).isEqualTo(1L);
    }
}
