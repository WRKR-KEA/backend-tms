package com.wrkr.tickety.domains.notification.application.dto;

import com.wrkr.tickety.domains.notification.domain.constant.tickety.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Schema(description = "전체 알림 조회 응답 DTO")
@Builder
public record ApplicationNotificationResponse(

    @Schema(description = "알림 PK", example = "ouqJF8uKst63ZPA2T70jda")
    String notificationId,

    @Schema(description = "회원 PK", example = "ouqJF8uKst63ZPA2T70jda")
    String memberId,

    @Schema(description = "프로필 이미지 url", example = "http://...")
    String profileImage,

    @Schema(description = "알림 내용", example = "티켓이 승인되었습니다.")
    String content,

    @Schema(description = "알림 구분 (TICKET | COMMENT | REMIND)", example = "TICKET")
    NotificationType type,

    @Schema(description = "읽음 여부 (true | false)", example = "true")
    Boolean isRead,

    @Schema(description = "경과 시간", example = "10분 전")
    String timeAgo,

    @Schema(description = "발송 일시", example = "2021-01-01T00:00:00")
    LocalDateTime createdAt
) {

}
