package com.wrkr.tickety.domains.ticket.presentation;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_ALLOWED;
import static com.wrkr.tickety.domains.ticket.exception.StatisticsErrorCode.ILLEGAL_STATISTICS_OPTION;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_MANAGER_NOT_MATCH;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_APPROVABLE;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_FOUND;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_REJECTABLE;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_STATUS_NOT_IN_PROGRESS;
import static com.wrkr.tickety.global.response.code.CommonErrorCode.METHOD_ARGUMENT_NOT_VALID;

import com.wrkr.tickety.domains.ticket.application.dto.request.StatisticsByCategoryRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.TicketDelegateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.ManagerTicketAllGetPagingResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.StatisticsByCategoryResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.ManagerTicketDetailResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsByCategoryUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketAllGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketDelegateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketDetailUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketApproveUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketRejectUseCase;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.utils.PkCrypto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Tag(name = "Manager Ticket Controller")
@RequestMapping("/api/manager")
public class ManagerTicketController {

    private final StatisticsByCategoryUseCase statisticsByCategoryUseCase;
    private final TicketApproveUseCase ticketApproveUseCase;
    private final TicketRejectUseCase ticketRejectUseCase;
    private final ManagerTicketDetailUseCase managerTicketDetailUseCase;
    private final ManagerTicketAllGetUseCase managerTicketAllGetUseCase;
    private final ManagerTicketDelegateUseCase managerTicketDelegateUseCase;
    private final StatisticsGetUseCase statisticsGetUseCase;

    @PostMapping("/statistics/{type}")
    @Operation(summary = "카테고리별 통계 조회")
    public ApplicationResponse<StatisticsByCategoryResponse> getStatistics(
        @Parameter(description = "통계 타입", example = "daily", required = true)
        @PathVariable String type,
        @Parameter(description = "통계를 확인하고자 하는 날짜", example = "2025-01-12", required = true)
        @RequestBody @Valid StatisticsByCategoryRequest request
    ) {
        StatisticsType statisticsType = StatisticsType.from(type);
        return ApplicationResponse.onSuccess(statisticsByCategoryUseCase.getStatisticsByCategory(statisticsType, request.date()));
    }

    @Operation(summary = "티켓 상세 조회", description = "특정 티켓을 상세 조회합니다.")
    @GetMapping("/tickets/{ticketId}")
    public ApplicationResponse<ManagerTicketDetailResponse> getManagerTicketDetail(
        @Schema(description = "티켓 ID", example = "W1NMMfAHGTnNGLdRL3lvcw") @PathVariable String ticketId
    ) {
        return ApplicationResponse.onSuccess(managerTicketDetailUseCase.getManagerTicketDetail(PkCrypto.decrypt(ticketId)));
    }

    @PatchMapping("/tickets/approve")
    @CustomErrorCodes(
        ticketErrorCodes = {TICKET_NOT_APPROVABLE, TICKET_NOT_FOUND},
        memberErrorCodes = {MEMBER_NOT_ALLOWED}
    )
    @Parameters({@Parameter(name = "ticketId", description = "티켓 PK 리스트", example = "['abc123', 'def456']", required = true)})
    @Operation(summary = "담당자 - 티켓 승인", description = "담당자가 티켓을 일괄 또는 개별 승인합니다.")
    public ApplicationResponse<List<TicketPkResponse>> approveTicket(
        @RequestParam(value = "memberId") String memberId,
        @RequestParam(value = "ticketId") List<String> ticketId
    ) {
        List<TicketPkResponse> response = ticketApproveUseCase.approveTicket(memberId, ticketId);
        return ApplicationResponse.onSuccess(response);
    }

    @PatchMapping("/{ticketId}/reject")
    @CustomErrorCodes(ticketErrorCodes = {TICKET_NOT_REJECTABLE, TICKET_NOT_FOUND, TICKET_MANAGER_NOT_MATCH})
    @Parameters({@Parameter(name = "ticketId", description = "티켓 PK", example = "abc123", required = true)})
    @Operation(summary = "담당자 - 티켓 반려", description = "담당자가 티켓을 반려합니다.")
    public ApplicationResponse<TicketPkResponse> rejectTicket(
        @RequestParam(value = "memberId") String memberId,
        @PathVariable(value = "ticketId") String ticketId
    ) {
        TicketPkResponse response = ticketRejectUseCase.rejectTicket(memberId, ticketId);
        return ApplicationResponse.onSuccess(response);
    }

    @Operation(summary = "담당자 담당 티켓 목록 요청", description = "담당자의 담당 티켓 목록을 요청합니다.")
    @GetMapping("/tickets/{managerId}")
    public ResponseEntity<ApplicationResponse<ManagerTicketAllGetPagingResponse>> getManagerTickets(
        @Schema(description = "담당자 ID", example = "Gbdsnz3dU0kwFxKpavlkog")
        @PathVariable String managerId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "티켓 상태 (REQUEST | IN_PROGRESS | COMPLETE | CANCEL | REJECT)", example = "IN_PROGRESS")
        @RequestParam(required = false) TicketStatus status,
        @Schema(description = "검색어")
        @RequestParam(required = false) String search
    ) {

        Pageable pageable = PageRequest.of(page, size);

        ManagerTicketAllGetPagingResponse ticketAllGetPagingResponse = managerTicketAllGetUseCase.getManagerTicketList(managerId, pageable, status, search);

        return ResponseEntity.ok(ApplicationResponse.onSuccess(ticketAllGetPagingResponse));
    }

    @Operation(summary = "해당 티켓 담당자 변경", description = "해당 티켓의 담당자를 변경합니다.")
    @PatchMapping("/tickets/{ticketId}/delegate")
    @CustomErrorCodes(ticketErrorCodes = {TICKET_NOT_FOUND, TICKET_MANAGER_NOT_MATCH, TICKET_STATUS_NOT_IN_PROGRESS})
    public ApplicationResponse<TicketPkResponse> delegateTicket(
        @PathVariable String ticketId,
        @Parameter(description = "티켓 담당자 변경 요청 정보", required = true)
        @Valid @RequestBody TicketDelegateRequest request
    ) {
        return ApplicationResponse.onSuccess(
            managerTicketDelegateUseCase.delegateTicket(PkCrypto.decrypt(ticketId), PkCrypto.decrypt(request.currentManagerId()), request)
        );
    }

    @Operation(summary = "기간별(일별 or 월별) & 티켓 상태별 티켓 개수 조회", description = "")
    @Parameters({
        @Parameter(name = "statisticsType", description = "통계 타입 (daily | monthly | yearly | total)", example = "daily"),
        @Parameter(name = "ticketStatus", description = "티켓 상태 (request | in_progress | complete | cancel | reject)", example = "in_progress"),
    })
    @CustomErrorCodes(
        commonErrorCodes = {METHOD_ARGUMENT_NOT_VALID},
        statisticsErrorCodes = {ILLEGAL_STATISTICS_OPTION}
    )
    @GetMapping("/statistics/count")
    public ApplicationResponse<StatisticsByTicketStatusResponse> getTicketCountStatistics(
        @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().toString()}") String date,
        @RequestParam(defaultValue = "TOTAL") StatisticsType statisticsType,
        @RequestParam(required = false) TicketStatus ticketStatus
    ) {
        return ApplicationResponse.onSuccess(
            statisticsGetUseCase.getTicketCountStatistics(date, statisticsType, ticketStatus)
        );
    }

}
