package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.ticket.RemindReceiverRequest;
import com.wrkr.tickety.domains.ticket.application.usecase.ticket.TicketReminderUseCase;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.utils.PkCrypto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Manager Ticket Controller")
@RequestMapping("/api/user/tickets")
public class TicketReminderController {

    private final TicketReminderUseCase ticketReminderUseCase;

    @PostMapping("/{ticketId}/remind")
    @Parameters({@Parameter(name = "ticketId", description = "티켓 PK", example = "abc123", required = true)})
    @Operation(summary = "리마인더 전송", description = "티켓의 요청자 또는 담당자가 리마인드 요청을 보냅니다.")
    public ApplicationResponse<Boolean> remind(
        @AuthenticationPrincipal Member member,
        @PathVariable(value = "ticketId") String ticketId,
        @RequestBody RemindReceiverRequest remindReceiverRequest
    ) {
        boolean response = ticketReminderUseCase.sendReminder(
            member.getMemberId(), PkCrypto.decrypt(remindReceiverRequest.memberId()), PkCrypto.decrypt(ticketId)
        );
        return ApplicationResponse.onSuccess(response);
    }
}
