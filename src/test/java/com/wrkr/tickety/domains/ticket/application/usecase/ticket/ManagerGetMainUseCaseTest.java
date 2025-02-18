package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.ManagerTicketMainPageResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.ManagerTicketMainPageResponse.PinTickets;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.ManagerTicketMainPageResponse.RequestTickets;
import com.wrkr.tickety.domains.ticket.application.mapper.TicketMapper;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManagerGetMainUseCaseTest {

    @InjectMocks
    private ManagerGetMainUseCase managerGetMainUseCase;

    @Mock
    private TicketGetService ticketGetService;

    private List<Ticket> pinTickets;
    private List<Ticket> requestTickets;
    private final Long MANAGER_ID = 1L;

    @BeforeEach
    void setUp() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();

        pinTickets = List.of(
            Ticket.builder()
                .isPinned(true)
                .build(),
            Ticket.builder().build()
        );

        requestTickets = List.of(
            Ticket.builder()
                .status(TicketStatus.REQUEST)
                .build(),
            Ticket.builder()
                .status(TicketStatus.REQUEST)
                .build()
        );
    }

    @Test
    @DisplayName("담당자 메인페이지 조회 시, 최근 요청 티켓 목록과 고정한 티켓 목록을 반환한다.")
    void getMain_Success() {
        //given
        given(ticketGetService.getPinTickets(MANAGER_ID)).willReturn(pinTickets);
        given(ticketGetService.getRequestTickets()).willReturn(requestTickets);

        ManagerTicketMainPageResponse managerTicketMainPageResponse = ManagerTicketMainPageResponse.builder()
            .pinTickets(pinTickets.stream()
                .map(ticket -> PinTickets.builder()
                    .build())
                .toList()
            )
            .requestTickets(requestTickets.stream()
                .map(ticket -> RequestTickets.builder()
                    .status(ticket.getStatus())
                    .build())
                .toList()
            )
            .build();

        try (MockedStatic<TicketMapper> ticketMapper = mockStatic(TicketMapper.class)) {
            ticketMapper.when(() -> TicketMapper.toManagerTicketMainPageResponse(pinTickets, requestTickets))
                .thenReturn(managerTicketMainPageResponse);
            //when
            ManagerTicketMainPageResponse response = managerGetMainUseCase.getMain(MANAGER_ID);

            //then
            assertThat(response).isEqualTo(managerTicketMainPageResponse);
        }

    }

    @Test
    @DisplayName("담당자 메인페이지 조회 시, 고정한 티켓 목록을 반환한다.")
    void getPinTickets() {
        //given
        given(ticketGetService.getPinTickets(MANAGER_ID)).willReturn(pinTickets);
        List<ManagerTicketMainPageResponse.PinTickets> pinTicketsResponse = pinTickets.stream()
            .map(ticket -> ManagerTicketMainPageResponse.PinTickets.builder()
                .build())
            .toList();

        try (MockedStatic<TicketMapper> ticketMapper = mockStatic(TicketMapper.class)) {
            for (Ticket ticket : pinTickets) {
                ticketMapper.when(() -> TicketMapper.toPinTicketsResponse(ticket))
                    .thenReturn(ManagerTicketMainPageResponse.PinTickets.builder()
                        .build());
            }
            //when
            List<ManagerTicketMainPageResponse.PinTickets> response = managerGetMainUseCase.getPinTickets(MANAGER_ID);

            //then
            assertThat(response).isEqualTo(pinTicketsResponse);
        }
    }

    @Test
    @DisplayName("담당자 메인페이지 조회 시, 최근 요청 티켓 목록을 반환한다.")
    void getRecentRequestTickets() {
        //given
        given(ticketGetService.getRequestTickets()).willReturn(requestTickets);
        List<RequestTickets> requestTicketsResponse = requestTickets.stream()
            .map(ticket -> RequestTickets.builder()
                .status(ticket.getStatus())
                .build())
            .toList();

        try (MockedStatic<TicketMapper> ticketMapper = mockStatic(TicketMapper.class)) {
            for (Ticket ticket : requestTickets) {
                ticketMapper.when(() -> TicketMapper.toRequestTicketsResponse(ticket))
                    .thenReturn(RequestTickets.builder()
                        .status(ticket.getStatus())
                        .build());
            }
            //when
            List<RequestTickets> response = managerGetMainUseCase.getRecentRequestTickets();

            //then
            assertThat(response).isEqualTo(requestTicketsResponse);
        }
    }
}