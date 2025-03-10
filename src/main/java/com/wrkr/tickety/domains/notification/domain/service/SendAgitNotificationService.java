package com.wrkr.tickety.domains.notification.domain.service;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.constant.agit.AgitCommentNotificationMessage;
import com.wrkr.tickety.domains.notification.domain.constant.agit.AgitTicketDelegateNotificationMessage;
import com.wrkr.tickety.domains.notification.domain.constant.agit.AgitTicketNotificationMessageType;
import com.wrkr.tickety.domains.notification.domain.constant.application.Remind;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
public class SendAgitNotificationService {

    private final WebClient webClient;

    @Async
    public void sendTicketStatusChangeAgitAlarm(Member member, Ticket ticket, AgitTicketNotificationMessageType agitTicketNotificationMessageType) {
        String agitUrl = member.getAgitUrl();
        String ticketSerialNumber = ticket.getSerialNumber();
        String message = switch (agitTicketNotificationMessageType) {
            case TICKET_APPROVED -> AgitTicketNotificationMessageType.TICKET_APPROVED.format(ticketSerialNumber);
            case TICKET_REJECT -> AgitTicketNotificationMessageType.TICKET_REJECT.format(ticketSerialNumber);
            case TICKET_FINISHED -> AgitTicketNotificationMessageType.TICKET_FINISHED.format(ticketSerialNumber);
        };
        requestAgitApi(agitUrl, message);
    }

    @Async
    public void sendCommentCreateAgitAlarm(Member receiver, Ticket ticket) {
        String message = AgitCommentNotificationMessage.COMMENT_UPDATE.format(ticket.getSerialNumber());
        String agitUrl = receiver.getAgitUrl();
        requestAgitApi(agitUrl, message);
    }

    @Async
    public void sendTicketDelegateAgitAlarmToUser(Member receiver, Member newManager, Ticket ticket) {
        String MessageToUser = AgitTicketDelegateNotificationMessage.TICKET_DELEGATE_MESSAGE_TO_USER.format(
            ticket.getSerialNumber(), newManager.getNickname()
        );
        requestAgitApi(receiver.getAgitUrl(), MessageToUser);
    }

    @Async
    public void sendTicketDelegateAgitAlarmToManager(Member receiver, Member prevManager, Ticket ticket) {
        String MessageToManager = AgitTicketDelegateNotificationMessage.TICKET_DELEGATE_MESSAGE_TO_NEW_MANAGER.format(
            prevManager.getNickname(), ticket.getSerialNumber()
        );
        requestAgitApi(receiver.getAgitUrl(), MessageToManager);
    }

    @Async
    public void sendRemindAgitAlarm(Member member, Ticket ticket) {
        String agitUrl = member.getAgitUrl();
        String message = Remind.REMIND_TICKET.format(ticket.getSerialNumber());
        requestAgitApi(agitUrl, message);
    }

    /**
     * agitUrl로 POST 요청을 보내는 메서드 요청을 보내는데 실패하면 최대 3번까지 재시도 전송 과정에서 예외가 발생하면 즉시 중단되고 재시도하지 않음
     */
    private void requestAgitApi(String agitUrl, String message) {
        String jsonPayload = String.format("{\"text\": \"%s\"}", message);

        webClient.post()
            .uri(agitUrl)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(jsonPayload)
            .retrieve()
            .toBodilessEntity()
            .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1))
                .filter(throwable -> !(throwable instanceof InterruptedException)))
            .block();
    }
}
