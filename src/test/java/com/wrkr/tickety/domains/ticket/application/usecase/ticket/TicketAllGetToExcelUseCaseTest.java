package com.wrkr.tickety.domains.ticket.application.usecase.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketExcelPreResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketExcelResponse;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.domain.service.ticket.TicketGetService;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import com.wrkr.tickety.global.utils.date.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TicketAllGetToExcelUseCaseTest {

    @Mock
    private TicketGetService ticketGetService;

    @InjectMocks
    private TicketAllGetToExcelUseCase ticketAllGetToExcelUseCase;

    private List<DepartmentTicketExcelPreResponse> mockPreResponses;

    @BeforeEach
    void setUp() {
        mockPreResponses = List.of(
            new DepartmentTicketExcelPreResponse("VM-12345678", TicketStatus.REQUEST, "VM 생성 요청", "VM", "생성", "request.er", "manage.r", LocalDateTime.now(),
                LocalDateTime.now())
        );
    }

    @Test
    @DisplayName("성공: 검색 조건 없이 전체 조회")
    void getAllTicketsNoPaging_Success() {
        given(ticketGetService.getDepartmentAllTicketsNoPaging(any(), any(), any(), any()))
            .willReturn(mockPreResponses);

        List<DepartmentTicketExcelResponse> result = ticketAllGetToExcelUseCase.getAllTicketsNoPaging(null, null, null, null);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).ticketSerialNumber()).isEqualTo("VM-12345678");
    }

    @Test
    @DisplayName("실패: 시작 날짜가 종료 날짜보다 늦은 경우 예외 발생")
    void getAllTicketsNoPaging_Fail_InvalidDateRange() {
        assertThatThrownBy(() -> ticketAllGetToExcelUseCase.getAllTicketsNoPaging(null, null, "2025-01-02", "2025-01-01"))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("성공: 특정 상태(TicketStatus)로 조회")
    void getAllTicketsNoPaging_Success_StatusFilter() {
        given(ticketGetService.getDepartmentAllTicketsNoPaging(any(), eq(TicketStatus.REQUEST), any(), any()))
            .willReturn(mockPreResponses);

        List<DepartmentTicketExcelResponse> result = ticketAllGetToExcelUseCase.getAllTicketsNoPaging(null, "REQUEST", null, null);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).status()).isEqualTo(TicketStatus.REQUEST.getDescription());
    }

    @Test
    @DisplayName("성공: 날짜 범위로 조회")
    void getAllTicketsNoPaging_Success_DateRange() {
        given(ticketGetService.getDepartmentAllTicketsNoPaging(any(), any(), eq(LocalDate.of(2025, 1, 1)), eq(LocalDate.of(2025, 1, 31))))
            .willReturn(mockPreResponses);

        List<DepartmentTicketExcelResponse> result = ticketAllGetToExcelUseCase.getAllTicketsNoPaging(null, null, "2025-01-01", "2025-01-31");

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).requestedDate()).isEqualTo(DateTimeFormatter.yyyyMMddHHmm(LocalDateTime.now()));
    }
}
