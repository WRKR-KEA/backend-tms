package com.wrkr.tickety.domains.notification.domain.service.kakaowork;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.application.dto.response.KakaoworkMessageResponse;
import com.wrkr.tickety.domains.notification.domain.constant.agit.AgitCommentNotificationMessage;
import com.wrkr.tickety.domains.notification.domain.constant.agit.AgitTicketDelegateNotificationMessage;
import com.wrkr.tickety.domains.notification.domain.constant.agit.AgitTicketNotificationMessageType;
import com.wrkr.tickety.domains.notification.domain.constant.application.Remind;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class KakaoworkNotificationService {

    private final WebClient webClient;

    @Value("${kakaowork.api.base-url}")
    private String baseUrl;

    @Value("${kakaowork.api.app-key}")
    private String appKey;

    public KakaoworkNotificationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<KakaoworkMessageResponse> sendMessageByEmail(String email, String text) {
        log.info("Sending message to email: {}, message: {}", email, text);

        String requestBody = String.format("{\"email\":\"%s\",\"text\":\"%s\"}", email, text);

        return webClient.post()
            .uri("/v1/messages.send_by_email")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + appKey)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(KakaoworkMessageResponse.class)
            .doOnError(e -> log.error("Error sending message by email: {}", e.getMessage()));
    }

    @Async
    public void sendTicketStatusChangeKakaoworkAlarm(Member member, Ticket ticket, AgitTicketNotificationMessageType agitTicketNotificationMessageType) {
        String ticketSerialNumber = ticket.getSerialNumber();
        String message = switch (agitTicketNotificationMessageType) {
            case TICKET_APPROVED -> AgitTicketNotificationMessageType.TICKET_APPROVED.format(ticketSerialNumber);
            case TICKET_REJECT -> AgitTicketNotificationMessageType.TICKET_REJECT.format(ticketSerialNumber);
            case TICKET_FINISHED -> AgitTicketNotificationMessageType.TICKET_FINISHED.format(ticketSerialNumber);
        };
        sendMessageByEmail(member.getEmail(), message).block();
    }

    @Async
    public void sendCommentCreateKakaoworkAlarm(Member receiver, Ticket ticket) {
        String message = AgitCommentNotificationMessage.COMMENT_UPDATE.format(ticket.getSerialNumber());
        sendMessageByEmail(receiver.getEmail(), message).block();
    }

    @Async
    public void sendTicketDelegateKakaoworkAlarmToUser(Member newManager, Ticket ticket) {
        String MessageToUser = AgitTicketDelegateNotificationMessage.TICKET_DELEGATE_MESSAGE_TO_USER.format(
            ticket.getSerialNumber(), newManager.getNickname()
        );
        sendMessageByEmail(ticket.getUser().getEmail(), MessageToUser).block();
    }

    @Async
    public void sendTicketDelegateKakaoworkAlarmToManager(Member prevManager, Member newManager, Ticket ticket) {
        String MessageToManager = AgitTicketDelegateNotificationMessage.TICKET_DELEGATE_MESSAGE_TO_NEW_MANAGER.format(
            prevManager.getNickname(), ticket.getSerialNumber()
        );
        sendMessageByEmail(newManager.getEmail(), MessageToManager).block();
    }

    @Async
    public void sendRemindKakaoworkAlarm(Member receiver, Ticket ticket) {
        String message = Remind.REMIND_TICKET.format(ticket.getSerialNumber());
        sendMessageByEmail(receiver.getEmail(), message).block();
    }
}
