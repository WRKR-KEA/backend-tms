package com.wrkr.tickety.domains.ticket.presentation;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_ALLOWED;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_MANAGER_NOT_MATCH;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_APPROVABLE;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_COMPLETABLE;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_DELEGATABLE;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_FOUND;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_REJECTABLE;
import static com.wrkr.tickety.global.response.code.CommonErrorCode.METHOD_ARGUMENT_NOT_VALID;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.TicketDelegateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.ManagerTicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.ManagerTicketDetailResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.DepartmentTicketAllGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketAllGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketDelegateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketDetailUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketApproveUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketCompleteUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketRejectUseCase;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.common.dto.PageRequest;
import com.wrkr.tickety.global.common.dto.PageResponse;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.utils.PkCrypto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ApplicationResponse<PageResponse<DepartmentTicketResponse>> getDepartmentTicket(
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
        PageRequest pageRequest
    ) {
        return ApplicationResponse.onSuccess(departmentTicketAllGetUseCase.getDepartmentTicketList(query, status, startDate, endDate, pageRequest));
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
    public ApplicationResponse<PageResponse<ManagerTicketAllGetResponse>> getManagerTickets(
        @AuthenticationPrincipal Member member,
        @Parameter(description = "페이징", example = "{\"page\":1,\"size\":20,\"sortType\":\"NEWEST\"}")
        PageRequest pageRequest,
        @Parameter(description = "티켓 상태 (REQUEST | IN_PROGRESS | COMPLETE | CANCEL | REJECT)", example = "IN_PROGRESS")
        @RequestParam(required = false) TicketStatus status,
        @Parameter(description = "검색어")
        @RequestParam(required = false) String query
    ) {
        PageResponse<ManagerTicketAllGetResponse> response = managerTicketAllGetUseCase.getManagerTicketList(
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
}
