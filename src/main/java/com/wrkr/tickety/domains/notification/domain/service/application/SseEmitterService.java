package com.wrkr.tickety.domains.notification.domain.service.application;

import static com.wrkr.tickety.domains.notification.application.mapper.NotificationMapper.toNotification;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.constant.NotificationType;
import com.wrkr.tickety.domains.notification.domain.model.Notification;
import com.wrkr.tickety.domains.notification.persistence.adapter.NotificationPersistenceAdapter;
import com.wrkr.tickety.domains.notification.persistence.repository.EmitterRepository;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private final EmitterRepository emitterRepository;
    private final NotificationPersistenceAdapter notificationPersistenceAdapter;

    private static final Long DEFAULT_TIMEOUT = 60L * 1000;

    public SseEmitter subscribe(Long memberId, String lastEventId) {
        String emitterId = memberId + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitter.complete());
        emitter.onError((error) -> emitter.complete());

        sendToClient(emitter, emitterId, "connect", "Subscribe EventStream: [memberId = " + memberId + "]");

        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
            events.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    public void send(Member member, NotificationType notificationType, String content) {
        Notification notification = notificationPersistenceAdapter.save(
            toNotification(member.getMemberId(), member.getProfileImage(), notificationType, content)
        );
        String memberId = String.valueOf(member.getMemberId());

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByMemberId(memberId);
        sseEmitters.forEach(
            (key, emitter) -> {
                emitterRepository.saveEventCache(key, notification);
                sendToClient(emitter, key, notificationType.name().toLowerCase(), content);
            }
        );
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                .id(emitterId)
                .data(data));
        } catch (IOException exception) {
            emitter.complete();
            log.warn("[SSE] Failed to send event: {}: {}", emitterId, exception.getMessage());
        }
    }

    private void sendToClient(SseEmitter emitter, String emitterId, String type, Object data) {
        try {
            emitter.send(SseEmitter.event()
                .id(emitterId)
                .name(type)
                .data(data));
        } catch (IOException exception) {
            emitter.complete();
            log.warn("[SSE] Failed to send event: {}: {}", emitterId, exception.getMessage());
        }
    }
}
