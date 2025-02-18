package com.wrkr.tickety.domains.notification.persistence.repository;

import java.util.Map;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {

    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId);

    void deleteById(String emitterId);
}
