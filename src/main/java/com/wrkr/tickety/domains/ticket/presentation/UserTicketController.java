package com.wrkr.tickety.domains.ticket.presentation;

import static com.wrkr.tickety.global.utils.PkCrypto.decrypt;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketDetailGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketPkResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.ticket.UserTicketMainPageResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketAllGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketCancelUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketDetailGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketGetMainUseCase;
import com.wrkr.tickety.domains.ticket.domain.constant.TicketStatus;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.common.dto.ApplicationPageRequest;
import com.wrkr.tickety.global.common.dto.ApplicationPageResponse;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/user/tickets")
@Tag(name = "User Ticket Controller")
public class UserTicketController {

    private final TicketCreateUseCase ticketCreateUseCase;
    private final TicketAllGetUseCase ticketAllGetUseCase;
    private final TicketDetailGetUseCase ticketDetailGetUseCase;
    private final TicketCancelUseCase ticketCancelUseCase;
    private final TicketGetMainUseCase ticketGetMainUseCase;

    @PostMapping
    @Operation(summary = "사용자 티켓 요청", description = "사용자가 티켓을 요청합니다.")
    @CustomErrorCodes(ticketErrorCodes = {})
    public ApplicationResponse<TicketPkResponse> createTicket(
        @AuthenticationPrincipal Member member,
        @Parameter(description = "티켓 요청 정보", required = true)
        @Valid @RequestBody TicketCreateRequest request
    ) {
        return ApplicationResponse.onSuccess(ticketCreateUseCase.createTicket(request, member.getMemberId()));
    }

    @GetMapping
    @Operation(summary = "사용자 요청 전체 티켓 조회", description = "사용자가 요청한 전체 티켓을 조회합니다.")
    @CustomErrorCodes(ticketErrorCodes = {})
    public ApplicationResponse<ApplicationPageResponse<TicketAllGetResponse>> getAllTickets(
        @AuthenticationPrincipal Member member,
        @Parameter(description = "페이징", example = "{\"page\":1,\"size\":20}")
        ApplicationPageRequest pageRequest,
        @Parameter(description = "티켓 상태 (REQUEST | IN_PROGRESS | COMPLETE | CANCEL | REJECT )")
        @RequestParam(value = "status", required = false) TicketStatus status
    ) {
        return ApplicationResponse.onSuccess(ticketAllGetUseCase.getAllTickets(member.getMemberId(), pageRequest, status));
    }

    @GetMapping("/{ticketId}")
    @Operation(summary = "사용자 요청한 특정 티켓 조회", description = "사용자가 요청한 특정 티켓을 조회합니다.")
    @CustomErrorCodes(ticketErrorCodes = {TicketErrorCode.TICKET_NOT_FOUND, TicketErrorCode.UNAUTHORIZED_ACCESS})
    public ApplicationResponse<TicketDetailGetResponse> getTicket(
        @AuthenticationPrincipal Member member,
        @PathVariable("ticketId") String ticketId
    ) {
        return ApplicationResponse.onSuccess(ticketDetailGetUseCase.getTicket(member.getMemberId(), decrypt(ticketId)));
    }

    @PatchMapping("/{ticketId}")
    @Operation(summary = "사용자 요청 티켓 취소", description = "사용자가 요청한 티켓을 취소합니다.")
    @CustomErrorCodes(ticketErrorCodes = {TicketErrorCode.TICKET_NOT_FOUND, TicketErrorCode.TICKET_NOT_BELONG_TO_USER,
        TicketErrorCode.TICKET_NOT_REQUEST_STATUS})
    public ApplicationResponse<TicketPkResponse> cancelTicket(
        @AuthenticationPrincipal Member member,
        @PathVariable("ticketId") String ticketId
    ) {
        return ApplicationResponse.onSuccess(ticketCancelUseCase.cancelTicket(member.getMemberId(), decrypt(ticketId)));
    }

    @GetMapping("/main")
    @Operation(summary = "사용자 메인 페이지 조회", description = "사용자의 메인 페이지를 조회합니다.")
    public ApplicationResponse<UserTicketMainPageResponse> mainPage(@AuthenticationPrincipal Member member) {
        return ApplicationResponse.onSuccess(ticketGetMainUseCase.getMain(member.getMemberId()));
    }
}
