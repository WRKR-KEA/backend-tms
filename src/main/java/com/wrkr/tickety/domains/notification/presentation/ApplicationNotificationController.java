package com.wrkr.tickety.domains.notification.presentation;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.application.dto.ApplicationNotificationResponse;
import com.wrkr.tickety.domains.notification.application.usecase.ApplicationNotificationGetUseCase;
import com.wrkr.tickety.global.response.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/notifications")
@Tag(name = "Application Notification Controller")
public class ApplicationNotificationController {

    private final ApplicationNotificationGetUseCase applicationNotificationGetUseCase;

    @Operation(summary = "알림 전체 조회", description = "관리자가 카테고리를 전체 조회합니다.")
    @GetMapping
    public ApplicationResponse<List<ApplicationNotificationResponse>> getAllNotifications(
        @AuthenticationPrincipal Member member
    ) {
        List<ApplicationNotificationResponse> notifications = applicationNotificationGetUseCase.getAllNotifications(member.getMemberId());
        return ApplicationResponse.onSuccess(notifications);
    }
}
