package com.wrkr.tickety.global.utils.date;

import java.time.Duration;
import java.time.LocalDateTime;

public class NotificationTimeFormatter {

    public static String formatRelativeTime(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());

        if (duration.toMinutes() < 1) {
            return "방금 전";
        } else if (duration.toMinutes() < 60) {
            return duration.toMinutes() + "분 전";
        } else if (duration.toHours() < 24) {
            return duration.toHours() + "시간 전";
        } else if (duration.toDays() == 1) {
            return "어제";
        } else {
            return duration.toDays() + "일 전";
        }
    }
}
