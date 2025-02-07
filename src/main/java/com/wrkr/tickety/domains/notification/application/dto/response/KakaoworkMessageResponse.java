package com.wrkr.tickety.domains.notification.application.dto.response;

public record KakaoworkMessageResponse(Message message, boolean success) {

    public record Message(String id, String text, Integer user_id, String conversation_id, Long send_time, Long update_time, Block[] blocks) {

    }

    public record Block(String type, String text, boolean markdown) {

    }
}
