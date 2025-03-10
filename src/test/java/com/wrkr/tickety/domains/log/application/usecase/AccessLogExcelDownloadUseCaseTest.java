package com.wrkr.tickety.domains.log.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.wrkr.tickety.domains.log.application.dto.response.AccessLogExcelResponse;
import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.domain.model.AccessLog;
import com.wrkr.tickety.domains.log.domain.service.AccessLogGetService;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
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
class AccessLogExcelDownloadUseCaseTest {

    @Mock
    private AccessLogGetService accessLogGetService;

    @InjectMocks
    private AccessLogExcelDownloadUseCase accessLogExcelDownloadUseCase;
    private List<AccessLog> logs;

    @BeforeEach
    void setUp() {
        logs = List.of(AccessLog.builder()
            .accessLogId(1L)
            .nickname("nickname")
            .ip("111.222.333.444")
            .action(ActionType.LOGIN)
            .accessAt(LocalDateTime.now())
            .isSuccess(true)
            .build());
    }

    @Test
    @DisplayName("정상 케이스 - 검색 조건 없이 전체 조회")
    void getAllAccessLogs_Success_NoFilters() {
        // given
        given(accessLogGetService.getAllAccessLogs(null, null, null, null)).willReturn(logs);

        // when
        List<AccessLogExcelResponse> response = accessLogExcelDownloadUseCase.getAllAccessLogs(null, null, null, null);

        // then
        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
    }

    @Test
    @DisplayName("정상 케이스 - 특정 액션 타입으로 검색")
    void getAllAccessLogs_Success_ActionFilter() {
        // given
        given(accessLogGetService.getAllAccessLogs(
            any(),
            eq(ActionType.LOGIN),
            any(LocalDate.class),
            any(LocalDate.class)
        )).willReturn(logs);

        // when
        List<AccessLogExcelResponse> response = accessLogExcelDownloadUseCase.getAllAccessLogs(null, ActionType.LOGIN, "2024-01-27", "2025-01-27");

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("정상 케이스 - 특정 날짜 범위 검색")
    void getAllAccessLogs_Success_DateRange() {
        // given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        given(accessLogGetService.getAllAccessLogs(null, null, startDate, endDate)).willReturn(logs);

        // when
        List<AccessLogExcelResponse> response = accessLogExcelDownloadUseCase.getAllAccessLogs(null, null, "2023-01-01", "2023-12-31");

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("실패 케이스 - 시작 날짜가 종료 날짜보다 늦음")
    void getAllAccessLogs_Fail_InvalidDateRange() {
        // given & when & then
        assertThatThrownBy(() -> accessLogExcelDownloadUseCase.getAllAccessLogs(null, null, "2023-12-31", "2023-01-01"))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("실패 케이스 - 잘못된 날짜 형식 입력")
    void getAllAccessLogs_Fail_InvalidDateFormat() {
        // when & then
        try {
            accessLogExcelDownloadUseCase.getAllAccessLogs(null, null, "invalid-date", "2023-01-01");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }


    @Test
    @DisplayName("실패 케이스 - 검색 결과가 없음")
    void getAllAccessLogs_Fail_NoResults() {
        // given
        given(accessLogGetService.getAllAccessLogs("notfound", null, null, null)).willReturn(List.of());

        // when
        List<AccessLogExcelResponse> response = accessLogExcelDownloadUseCase.getAllAccessLogs("notfound", null, null, null);

        // then
        assertThat(response).isEmpty();
    }
}
