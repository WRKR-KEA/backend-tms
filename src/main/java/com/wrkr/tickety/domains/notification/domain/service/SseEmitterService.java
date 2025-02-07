package com.wrkr.tickety.domains.notification.domain.service;

import static com.wrkr.tickety.domains.notification.application.mapper.NotificationMapper.toNotification;

import com.wrkr.tickety.domains.notification.domain.constant.tickety.NotificationType;
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

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    public SseEmitter subscribe(Long memberId, String lastEventId) {
        String emitterId = memberId + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        sendToClient(emitter, emitterId, "Subscribe EventStream: [memberId = " + memberId + "]");

        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
            events.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    public void send(Long receiverId, NotificationType notificationType, String content) {
        Notification notification = notificationPersistenceAdapter.save(toNotification(receiverId, notificationType, content));
        String memberId = String.valueOf(receiverId);

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByMemberId(memberId);
        sseEmitters.forEach(
            (key, emitter) -> {
                emitterRepository.saveEventCache(key, notification);
                sendToClient(emitter, key, content);
            }
        );
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                .id(emitterId)
                .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            log.info("Failed to send emitter: " + emitterId + ": " + exception.getMessage());
        }
    }
}
