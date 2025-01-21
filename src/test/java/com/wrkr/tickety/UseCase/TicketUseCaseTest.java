package com.wrkr.tickety.UseCase;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.ticket.application.dto.request.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.TicketCreateUseCase;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.TicketHistorySaveService;
import com.wrkr.tickety.domains.ticket.domain.service.TicketSaveService;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketUseCaseTest {

    @Mock
    private TicketSaveService ticketSaveService;

    @Mock
    private CategoryGetService categoryGetService;

    @Mock
    private TicketHistorySaveService ticketHistorySaveService;

    @Mock
    private MemberGetService memberGetService;

    @InjectMocks
    private TicketCreateUseCase ticketCreateUseCase;

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

    // TODO: PK 암호화 문제 및 숙지 미달로 인한 테스트 코드 구현 잠시 보류
//    @Test
//    @DisplayName("사용자가 요청한 티켓이 저장되면 저장된 티켓 정보를 검증한다")
//    void createTicket() {
//        // Given
//        Long userId = 2L;
//        TicketCreateRequest request = new TicketCreateRequest("VM-001", "티켓 제목", "Bqs3C822lkMNdWlmE-szUw");
//
//        Category category = Category.builder()
//                .name("VM")
//                .code("VM-001")
//                .seq(1)
//                .build();
//
//        Ticket ticket = Ticket.builder()
//                .user(user)
//                .manager(manager)
//                .category(category)
//                .serialNumber("#12345678")
//                .title("티켓 제목")
//                .content("티켓 내용")
//                .status(TicketStatus.REQUEST)
//                .build();
//
//        when(categoryGetService.getCategory(any())).thenReturn(java.util.Optional.of(category));
//        when(ticketSaveService.save(any(Ticket.class))).thenReturn(ticket);
//
//        try (MockedStatic<PkCrypto> mockedPkCrypto = Mockito.mockStatic(PkCrypto.class)) {
//            mockedPkCrypto.when(() -> PkCrypto.decrypt("cAgGHRR6cLDIUW2fZ7Q3YA")).thenReturn(2L);
//            mockedPkCrypto.when(() -> PkCrypto.encrypt(2L)).thenReturn("cAgGHRR6cLDIUW2fZ7Q3YA");
//
//            // When
//            PkResponse response = ticketCreateUseCase.createTicket(request, userId);
//
//            // Then
//            verify(categoryGetService).getCategory(any());
//            verify(ticketSaveService).save(any(Ticket.class));
//            verify(ticketHistorySaveService).save(any());
//
//            assertThat(response).isNotNull();
//            assertThat(response.id()).isEqualTo("encrypted-id");
//        }
//    }
}
