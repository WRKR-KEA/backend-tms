package com.wrkr.tickety.domains.ticket.presentation;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_ALLOWED;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_MANAGER_NOT_MATCH;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_APPROVABLE;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_FOUND;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_STATUS_NOT_IN_PROGRESS;
import static com.wrkr.tickety.global.response.code.CommonErrorCode.METHOD_ARGUMENT_NOT_VALID;

import com.wrkr.tickety.domains.ticket.application.dto.request.TicketDelegateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.DepartmentTicketRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.ManagerTicketAllGetPagingResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.DepartmentTicketResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.ManagerTicketDetailResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.DepartmentTicketUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketAllGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketDelegateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketDetailUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketApproveUseCase;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.common.dto.PageResponse;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager/tickets")
@Tag(name = "Manager Ticket Controller")
public class ManagerTicketController {

    private final ManagerTicketDetailUseCase managerTicketDetailUseCase;
    private final DepartmentTicketUseCase departmentTicketUseCase;
    private final TicketApproveUseCase ticketApproveUseCase;
    private final ManagerTicketAllGetUseCase managerTicketAllGetUseCase;
    private final ManagerTicketDelegateUseCase managerTicketDelegateUseCase;

    @Operation(summary = "티켓 상세 조회", description = "특정 티켓을 상세 조회합니다.")
    @GetMapping("/{ticketId}")
    @CustomErrorCodes(ticketErrorCodes = {TICKET_NOT_FOUND})
    public ApplicationResponse<ManagerTicketDetailResponse> getManagerTicketDetail(
        @Schema(description = "티켓 ID", example = "W1NMMfAHGTnNGLdRL3lvcw") @PathVariable String ticketId
    ) {
        return ApplicationResponse.onSuccess(managerTicketDetailUseCase.getManagerTicketDetail(PkCrypto.decrypt(ticketId)));
    }

    @Operation(summary = "부서 전체 티켓 조회 및 검색", description = "부서 내부의 모든 티켓을 조회합니다.")
    @GetMapping("/department")
    @CustomErrorCodes(commonErrorCodes = {METHOD_ARGUMENT_NOT_VALID})
    public ApplicationResponse<PageResponse<DepartmentTicketResponse>> getDepartmentTicket(
        DepartmentTicketRequest request,
        @Schema(description = "페이징", nullable = true, example = "{\"page\":1,\"size\":20}")
        Pageable pageable
    ) {
        return ApplicationResponse.onSuccess(departmentTicketUseCase.getDepartmentTicket(request, pageable));
    }

    @PatchMapping("/approve")
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

    @Operation(summary = "담당자 담당 티켓 목록 요청", description = "담당자의 담당 티켓 목록을 요청합니다.")
    @GetMapping("/{managerId}")
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
    @PatchMapping("/{ticketId}/delegate")
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
}
