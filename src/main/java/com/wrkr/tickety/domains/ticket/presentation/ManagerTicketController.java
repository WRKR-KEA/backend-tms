package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.ticket.application.dto.response.TicketAllGetPagingResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.ManagerTicketAllGetUseCase;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager/tickets")
@Tag(name = "Manager Ticket Controller")
public class ManagerTicketController {

    private final ManagerTicketAllGetUseCase managerTicketAllGetUseCase;

    @Operation(summary = "담당자 담당 티켓 목록 요청", description = "담당자의 담당 티켓 목록을 요청합니다.")
    @GetMapping("/{managerId}")
    public ResponseEntity<ApplicationResponse<TicketAllGetPagingResponse>> getManagerTickets(
            @PathVariable String managerId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) boolean isPinned) {

        Pageable pageable = PageRequest.of(page, size);

        TicketAllGetPagingResponse ticketAllGetPagingResponse = managerTicketAllGetUseCase.getManagerTicketList(managerId, pageable, status, isPinned);

        return ResponseEntity.ok(ApplicationResponse.onSuccess(ticketAllGetPagingResponse));
    }
}
