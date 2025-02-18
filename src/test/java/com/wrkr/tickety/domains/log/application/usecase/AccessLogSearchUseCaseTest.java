package com.wrkr.tickety.domains.log.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.wrkr.tickety.domains.log.application.dto.response.AccessLogSearchResponse;
import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.domain.model.AccessLog;
import com.wrkr.tickety.domains.log.domain.service.AccessLogGetService;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import java.time.LocalDate;
import java.util.List;
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
class AccessLogSearchUseCaseTest {

    @Mock
    private AccessLogGetService accessLogGetService;

    @InjectMocks
    private AccessLogSearchUseCase accessLogSearchUseCase;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("정상 케이스 - 검색 조건 없이 전체 조회")
    void searchAccessLogs_Success_NoFilters() {
        // given
        List<AccessLog> logs = List.of(AccessLog.builder().build());
        Page<AccessLog> logPage = new PageImpl<>(logs, pageable, logs.size());

        given(accessLogGetService.searchAccessLogs(pageable, null, null, null, null)).willReturn(logPage);

        // when
        ApplicationPageResponse<AccessLogSearchResponse> response = accessLogSearchUseCase.searchAccessLogs(pageable, null, null, null, null);

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("정상 케이스 - 특정 액션 타입으로 검색")
    void searchAccessLogs_Success_ActionFilter() {
        // given
        List<AccessLog> logs = List.of(AccessLog.builder().build());
        Page<AccessLog> logPage = new PageImpl<>(logs, pageable, logs.size());
        given(accessLogGetService.searchAccessLogs(pageable, null, ActionType.LOGIN, null, null)).willReturn(logPage);

        // when
        ApplicationPageResponse<AccessLogSearchResponse> response = accessLogSearchUseCase.searchAccessLogs(pageable, null, ActionType.LOGIN, null, null);

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("정상 케이스 - 특정 날짜 범위 검색")
    void searchAccessLogs_Success_DateRange() {
        // given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        List<AccessLog> logs = List.of(AccessLog.builder().build());
        Page<AccessLog> logPage = new PageImpl<>(logs, pageable, logs.size());
        given(accessLogGetService.searchAccessLogs(pageable, null, null, startDate, endDate)).willReturn(logPage);

        // when
        ApplicationPageResponse<AccessLogSearchResponse> response = accessLogSearchUseCase.searchAccessLogs(pageable, null, null, "2023-01-01", "2023-12-31");

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("실패 케이스 - 시작 날짜가 종료 날짜보다 늦음")
    void searchAccessLogs_Fail_InvalidDateRange() {
        // given & when & then
        assertThatThrownBy(() -> accessLogSearchUseCase.searchAccessLogs(pageable, null, null, "2023-12-31", "2023-01-01"))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("실패 케이스 - 잘못된 날짜 형식 입력")
    void searchAccessLogs_Fail_InvalidDateFormat() {
        // given & when & then
        assertThatThrownBy(() -> accessLogSearchUseCase.searchAccessLogs(pageable, null, null, "invalid-date", "2023-01-01"))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("실패 케이스 - 검색 결과가 없음")
    void searchAccessLogs_Fail_NoResults() {
        // given
        Page<AccessLog> emptyPage = Page.empty();
        given(accessLogGetService.searchAccessLogs(pageable, "notfound", null, null, null)).willReturn(emptyPage);

        // when
        ApplicationPageResponse<AccessLogSearchResponse> response = accessLogSearchUseCase.searchAccessLogs(pageable, "notfound", null, null, null);

        // then
    }
}
