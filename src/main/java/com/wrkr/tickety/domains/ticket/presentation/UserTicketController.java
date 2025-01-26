package com.wrkr.tickety.domains.ticket.presentation;

import static com.wrkr.tickety.global.utils.PkCrypto.decrypt;

import com.wrkr.tickety.domains.ticket.application.dto.request.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketAllGetPagingResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketDetailGetResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketAllGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketCancelUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketDetailGetUseCase;
import com.wrkr.tickety.domains.ticket.exception.TicketErrorCode;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @PostMapping
    @Operation(summary = "사용자 티켓 요청", description = "사용자의 티켓을 요청합니다.")
    @CustomErrorCodes(ticketErrorCodes = {})
    public ApplicationResponse<PkResponse> createTicket(
        @RequestParam(value = "userId") Long userId,
        @Parameter(description = "티켓 요청 정보", required = true)
        @Valid @RequestBody TicketCreateRequest request
    ) {
        return ApplicationResponse.onSuccess(ticketCreateUseCase.createTicket(request, userId));
    }

    @GetMapping
    @Operation(summary = "사용자 요청 전체 티켓 조회", description = "사용자가 요청했던 전체 티켓을 조회합니다.")
    @CustomErrorCodes(ticketErrorCodes = {})
    public ApplicationResponse<TicketAllGetPagingResponse> getAllTickets(
        @RequestParam(value = "userId") Long userId,
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApplicationResponse.onSuccess(ticketAllGetUseCase.getAllTickets(userId, pageable));
    }

    @GetMapping("/{ticketId}")
    @Operation(summary = "사용자 요청한 특정 티켓 조회", description = "사용자가 요청한 특정 티켓을 조회합니다.")
    @CustomErrorCodes(ticketErrorCodes = {TicketErrorCode.TICKET_NOT_FOUND, TicketErrorCode.UNAUTHORIZED_ACCESS})
    public ApplicationResponse<TicketDetailGetResponse> getTicket(
        @RequestParam(value = "userId") Long userId,
        @PathVariable("ticketId") String ticketId
    ) {
        return ApplicationResponse.onSuccess(ticketDetailGetUseCase.getTicket(userId, decrypt(ticketId)));
    }

    @PatchMapping("/{ticketId}")
    @Operation(summary = "사용자 요청 티켓 취소", description = "사용자가 요청한 티켓을 취소합니다.")
    @CustomErrorCodes(ticketErrorCodes = {TicketErrorCode.TICKET_NOT_FOUND, TicketErrorCode.TICKET_NOT_BELONG_TO_USER,
        TicketErrorCode.TICKET_NOT_REQUEST_STATUS})
    public ApplicationResponse<PkResponse> cancelTicket(
        @RequestParam(value = "userId") Long userId,
        @PathVariable("ticketId") String ticketId
    ) {
        return ApplicationResponse.onSuccess(ticketCancelUseCase.cancelTicket(userId, decrypt(ticketId)));
    }
}
