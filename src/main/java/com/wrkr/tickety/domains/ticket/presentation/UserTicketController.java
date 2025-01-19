package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.ticket.application.dto.request.TicketCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.TicketAllGetResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.TicketAllGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.TicketCreateUseCase;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/tickets")
@Tag(name = "Ticket Controller")
public class UserTicketController {

    private final TicketCreateUseCase ticketCreateUseCase;
    private final TicketAllGetUseCase ticketAllGetUseCase;

    @PostMapping
    @Operation(summary = "사용자 티켓 요청", description = "사용자의 티켓을 요청합니다.")
    public ApplicationResponse<String> createTicket(
            @Parameter(description = "티켓 요청 정보", required = true)
            @Valid @RequestBody TicketCreateRequest request
            ) {
        String response = ticketCreateUseCase.createTicket(request);
        return ApplicationResponse.onSuccess(response);
    }

    @GetMapping
    @Operation(summary = "사용자 요청한 전체 티켓 조회", description = "사용자가 요청한 전체 티켓을 조회합니다.")
    public ApplicationResponse<TicketAllGetResponse> getAllTickets(
            @Parameter(hidden = true) @RequestParam(value = "userId") String userId,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApplicationResponse.onSuccess(ticketAllGetUseCase.getAllTickets(userId, pageable));
    }


}
