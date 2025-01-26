package com.wrkr.tickety.domains.ticket.presentation;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_ALLOWED;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_APPROVABLE;
import static com.wrkr.tickety.domains.ticket.exception.TicketErrorCode.TICKET_NOT_FOUND;

import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketApproveUseCase;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager/tickets")
@Tag(name = "Manager Ticket Controller")
public class ManagerTicketController {

    private final TicketApproveUseCase ticketApproveUseCase;

    @PatchMapping("/approve")
    @CustomErrorCodes(
        ticketErrorCodes = {TICKET_NOT_APPROVABLE, TICKET_NOT_FOUND},
        memberErrorCodes = {MEMBER_NOT_ALLOWED}
    )
    @Parameters({
        @Parameter(name = "ticketId", description = "티켓 PK 리스트", example = "['abc123', 'def456']", required = true)
    })
    @Operation(summary = "담당자 - 티켓 승인", description = "담당자가 티켓을 일괄 또는 개별 승인합니다.")
    public ApplicationResponse<List<PkResponse>> approveTicket(
        @RequestParam(value = "memberId") String memberId,
        @RequestParam(value = "ticketId") List<String> ticketId
    ) {
        List<PkResponse> response = ticketApproveUseCase.approveTicket(memberId, ticketId);
        return ApplicationResponse.onSuccess(response);
    }
}
