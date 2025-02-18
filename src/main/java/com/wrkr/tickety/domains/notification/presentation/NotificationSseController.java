package com.wrkr.tickety.domains.notification.presentation;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.service.application.SseEmitterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/sse")
@Tag(name = "Notification SSE Controller")
public class NotificationSseController {

    private final SseEmitterService sseEmitterService;

    @Operation(summary = "SSE 구독", description = "실시간 알림을 위해 SSE 구독을 진행합니다.")
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
        @AuthenticationPrincipal Member member
    ) {
        return sseEmitterService.subscribe(member.getMemberId());
    }
}
