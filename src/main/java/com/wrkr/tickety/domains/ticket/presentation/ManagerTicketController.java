package com.wrkr.tickety.domains.ticket.presentation;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_ALLOWED;
import static com.wrkr.tickety.domains.ticket.exception.StatisticsErrorCode.ILLEGAL_STATISTICS_OPTION;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_MANAGER_NOT_MATCH;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_APPROVABLE;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_COMPLETABLE;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_DELEGATABLE;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_FOUND;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_REJECTABLE;
import static com.wrkr.tickety.global.response.code.CommonErrorCode.METHOD_ARGUMENT_NOT_VALID;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.StatisticsByCategoryRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.TicketDelegateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.TicketPinRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.ManagerTicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.StatisticsByCategoryResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.statistics.StatisticsByTicketStatusResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.ManagerTicketDetailResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsByCategoryUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.statistics.StatisticsGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.DepartmentTicketAllGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketAllGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketDelegateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketDetailUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketPinUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketAllGetToExcelUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketApproveUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketCompleteUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketRejectUseCase;
import com.wrkr.tickety.domains.ticket.domain.constant.StatisticsType;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.excel.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/manager/tickets")
public class ManagerTicketController {

    private final TicketApproveUseCase ticketApproveUseCase;
    private final TicketRejectUseCase ticketRejectUseCase;
    private final TicketCompleteUseCase ticketCompleteUseCase;
    private final ManagerTicketDetailUseCase managerTicketDetailUseCase;
    private final DepartmentTicketAllGetUseCase departmentTicketAllGetUseCase;
    private final ManagerTicketAllGetUseCase managerTicketAllGetUseCase;
    private final ManagerTicketDelegateUseCase managerTicketDelegateUseCase;
    private final ManagerTicketPinUseCase managerTicketPinUseCase;
    private final StatisticsGetUseCase statisticsGetUseCase;
    private final TicketAllGetToExcelUseCase ticketAllGetToExcelUseCase;
    private final ExcelUtil excelUtil;
    private final StatisticsByCategoryUseCase statisticsByCategoryUseCase;

    @PostMapping("/statistics/{statisticsType}")
    @Operation(summary = "카테고리별 통계 조회")
    public ApplicationResponse<StatisticsByCategoryResponse> getStatistics(
        @AuthenticationPrincipal Member member,
        @Parameter(description = "통계 타입", example = "daily", required = true) @PathVariable StatisticsType statisticsType,
        @Parameter(description = "통계를 확인하고자 하는 날짜", example = "2025-01-12", required = true) @RequestBody @Valid StatisticsByCategoryRequest request
    ) {
        return ApplicationResponse.onSuccess(statisticsByCategoryUseCase.getStatisticsByCategory(statisticsType, request.date()));
    }

    @Operation(summary = "티켓 상세 조회", description = "특정 티켓을 상세 조회합니다.")
    @GetMapping("/{ticketId}")
    @CustomErrorCodes(ticketErrorCodes = {TICKET_NOT_FOUND})
    public ApplicationResponse<ManagerTicketDetailResponse> getManagerTicketDetail(
        @AuthenticationPrincipal Member member,
        @Parameter(description = "티켓 ID", example = "W1NMMfAHGTnNGLdRL3lvcw") @PathVariable String ticketId
    ) {
        return ApplicationResponse.onSuccess(managerTicketDetailUseCase.getManagerTicketDetail(PkCrypto.decrypt(ticketId)));
    }

    @Operation(summary = "부서 전체 티켓 조회 및 검색", description = "부서 내부의 모든 티켓을 조회합니다.")
    @GetMapping("/department")
    @CustomErrorCodes(commonErrorCodes = {METHOD_ARGUMENT_NOT_VALID})
    public ApplicationResponse<ApplicationPageResponse<DepartmentTicketResponse>> getDepartmentTicket(
        @AuthenticationPrincipal Member member,
        @Parameter(description = "검색어 (제목, 담당자, 티켓 번호 대상)", example = "VM")
        @RequestParam(required = false) String query,
        @Parameter(description = "필터링 - 티켓 상태 (REQUEST | IN_PROGRESS | COMPLETE | CANCEL | REJECT)", example = "IN_PROGRESS")
        @RequestParam(required = false) String status,
        @Parameter(description = "필터링 - 요청일 시작", example = "2024-01-27")
        @RequestParam(required = false) String startDate,
        @Parameter(description = "필터링 - 요청일 끝", example = "2025-01-27")
        @RequestParam(required = false) String endDate,
        @Parameter(description = "페이징", example = "{\"page\":1,\"size\":20,\"sortType\":\"UPDATED\"}")
        ApplicationPageRequest pageRequest
    ) {
        return ApplicationResponse.onSuccess(departmentTicketAllGetUseCase.getDepartmentTicketList(query, status, startDate, endDate, pageRequest));
    }

    @Operation(summary = "부서 티켓 목록 엑셀 다운로드(상태별)", description = "부서 내부의 모든 티켓을 조회해서 엑셀 파일로 반환합니다.")
    @GetMapping("/tickets/department/excel")
    public void getDepartmentAllTicketsExcelDownload(
        HttpServletResponse response,
        @Parameter(description = "검색어 (제목, 담당자, 티켓 번호 대상)", example = "VM")
        @RequestParam(required = false) String query,
        @Parameter(description = "필터링 - 티켓 상태 (REQUEST | IN_PROGRESS | COMPLETE | CANCEL | REJECT)", example = "IN_PROGRESS")
        @RequestParam(required = false) String status,
        @Parameter(description = "필터링 - 요청일 시작", example = "2025-01-27")
        @RequestParam(required = false) String startDate,
        @Parameter(description = "필터링 - 요청일 끝", example = "2025-01-27")
        @RequestParam(required = false) String endDate
    ) {
        List<DepartmentTicketResponse> allTicketsNoPaging = ticketAllGetToExcelUseCase.getAllTicketsNoPaging(query, status, startDate, endDate);

        // TODO: 파일 이름 생성 정책 정하기
        excelUtil.parseTicketDataToExcelGroupByStatus(response, allTicketsNoPaging, "ticket2025");
    }

    @PatchMapping("/approve")
    @CustomErrorCodes(
        ticketErrorCodes = {TICKET_NOT_APPROVABLE, TICKET_NOT_FOUND},
        memberErrorCodes = {MEMBER_NOT_ALLOWED}
    )
    @Parameters({@Parameter(name = "ticketId", description = "티켓 PK 리스트", example = "['abc123', 'def456']", required = true)})
    @Operation(summary = "담당자 - 티켓 승인", description = "담당자가 티켓을 일괄 또는 개별 승인합니다.")
    public ApplicationResponse<List<TicketPkResponse>> approveTicket(
        @AuthenticationPrincipal Member member,
        @RequestParam(value = "ticketId") List<String> ticketId
    ) {
        List<TicketPkResponse> response = ticketApproveUseCase.approveTicket(member.getMemberId(), ticketId);
        return ApplicationResponse.onSuccess(response);
    }

    @PatchMapping("/{ticketId}/reject")
    @CustomErrorCodes(ticketErrorCodes = {TICKET_NOT_REJECTABLE, TICKET_NOT_FOUND, TICKET_MANAGER_NOT_MATCH})
    @Parameters({@Parameter(name = "ticketId", description = "티켓 PK", example = "abc123", required = true)})
    @Operation(summary = "담당자 - 티켓 반려", description = "담당자가 티켓을 반려합니다.")
    public ApplicationResponse<TicketPkResponse> rejectTicket(
        @AuthenticationPrincipal Member member,
        @PathVariable(value = "ticketId") String ticketId
    ) {
        TicketPkResponse response = ticketRejectUseCase.rejectTicket(member.getMemberId(), ticketId);
        return ApplicationResponse.onSuccess(response);
    }

    @PatchMapping("/{ticketId}/complete")
    @CustomErrorCodes(ticketErrorCodes = {TICKET_NOT_COMPLETABLE, TICKET_NOT_FOUND, TICKET_MANAGER_NOT_MATCH})
    @Parameters({@Parameter(name = "ticketId", description = "티켓 PK", example = "abc123", required = true)})
    @Operation(summary = "담당자 - 티켓 완료", description = "담당자가 티켓을 완료합니다.")
    public ApplicationResponse<TicketPkResponse> completeTicket(
        @AuthenticationPrincipal Member member,
        @PathVariable(value = "ticketId") String ticketId
    ) {
        TicketPkResponse response = ticketCompleteUseCase.completeTicket(member.getMemberId(), ticketId);
        return ApplicationResponse.onSuccess(response);
    }

    @Operation(summary = "담당자 담당 티켓 목록 요청", description = "담당자의 담당 티켓 목록을 요청합니다.")
    @GetMapping()
    public ApplicationResponse<ApplicationPageResponse<ManagerTicketAllGetResponse>> getManagerTickets(
        @AuthenticationPrincipal Member member,
        @Parameter(description = "페이징", example = "{\"page\":1,\"size\":20,\"sortType\":\"NEWEST\"}")
        ApplicationPageRequest pageRequest,
        @Parameter(description = "티켓 상태 (REQUEST | IN_PROGRESS | COMPLETE | CANCEL | REJECT)", example = "IN_PROGRESS")
        @RequestParam(required = false) TicketStatus status,
        @Parameter(description = "검색어")
        @RequestParam(required = false) String query
    ) {
        ApplicationPageResponse<ManagerTicketAllGetResponse> response = managerTicketAllGetUseCase.getManagerTicketList(
            member.getMemberId(), pageRequest, status, query
        );

        return ApplicationResponse.onSuccess(response);
    }

    @Operation(summary = "해당 티켓 담당자 변경", description = "해당 티켓의 담당자를 변경합니다.")
    @PatchMapping("/{ticketId}/delegate")
    @CustomErrorCodes(ticketErrorCodes = {TICKET_NOT_FOUND, TICKET_MANAGER_NOT_MATCH, TICKET_NOT_DELEGATABLE})
    public ApplicationResponse<TicketPkResponse> delegateTicket(
        @AuthenticationPrincipal Member member,
        @PathVariable String ticketId,
        @Parameter(description = "티켓 담당자 변경 요청 정보", required = true)
        @Valid @RequestBody TicketDelegateRequest request
    ) {
        return ApplicationResponse.onSuccess(managerTicketDelegateUseCase.delegateTicket(PkCrypto.decrypt(ticketId), member.getMemberId(), request));
    }

    @Operation(summary = "기간별 & 티켓 상태별 티켓 개수 조회", description = "기간별 & 티켓 상태별로 티켓의 개수를 조회합니다.")
    @Parameters({
        @Parameter(name = "type", description = "통계 타입 (DAILY | MONTHLY | YEARLY | TOTAL)", example = "DAILY"),
        @Parameter(name = "status", description = "티켓 상태 (REQUEST | IN_PROGRESS | COMPLETE | CANCEL | REJECT)", example = "IN_PROGRESS"),
    })
    @CustomErrorCodes(
        commonErrorCodes = {METHOD_ARGUMENT_NOT_VALID},
        statisticsErrorCodes = {ILLEGAL_STATISTICS_OPTION}
    )
    @GetMapping("/statistics/count")
    public ApplicationResponse<StatisticsByTicketStatusResponse> getTicketCountStatistics(
        @AuthenticationPrincipal Member member,
        @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().toString()}") String date,
        @RequestParam(defaultValue = "TOTAL") StatisticsType type,
        @RequestParam(required = false) TicketStatus status
    ) {
        return ApplicationResponse.onSuccess(
            statisticsGetUseCase.getTicketCountStatistics(date, type, status)
        );
    }

    @Operation(summary = "해당 티켓 상단 고정", description = "해당 티켓을 상단 고정합니다.")
    @CustomErrorCodes(ticketErrorCodes = {TICKET_NOT_FOUND, TICKET_MANAGER_NOT_MATCH})
    @PatchMapping("/tickets/pin")
    public ApplicationResponse<TicketPkResponse> pinTicket(@RequestBody TicketPinRequest request) {
        return ApplicationResponse.onSuccess(managerTicketPinUseCase.pinTicket(request));
    }
}
