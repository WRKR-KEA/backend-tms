package com.wrkr.tickety.domains.alarm.domain.service;

import com.wrkr.tickety.domains.alarm.domain.constant.AgitCommentAlarmMessage;
import com.wrkr.tickety.domains.alarm.domain.constant.AgitTicketAlarmMessageType;
import com.wrkr.tickety.domains.alarm.domain.constant.AgitTicketDelegateAlarmMessage;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.stereotype.Service;

@Service
public class SendAgitAlarmService {

    public void sendTicketStatusChangeAgitAlarm(Member member, Ticket ticket, AgitTicketAlarmMessageType agitTicketAlarmMessageType) {
        String agitUrl = member.getAgitUrl();
        String ticketSerialNumber = ticket.getSerialNumber();
        String message = switch (agitTicketAlarmMessageType) {
            case TICKET_APPROVED -> AgitTicketAlarmMessageType.TICKET_APPROVED.format(ticketSerialNumber);
            case TICKET_REJECT -> AgitTicketAlarmMessageType.TICKET_REJECT.format(ticketSerialNumber);
            case TICKET_FINISHED -> AgitTicketAlarmMessageType.TICKET_FINISHED.format(ticketSerialNumber);
        };
        requestAgitApi(agitUrl, message);
    }

    public void sendCommentCreateAgitAlarm(Member receiver, Ticket ticket) {
        String ticketSerialNumber = AgitCommentAlarmMessage.COMMENT_UPDATE.format(ticket.getSerialNumber());
        String agitUrl = receiver.getAgitUrl();
        String message = AgitCommentAlarmMessage.COMMENT_UPDATE.format(ticketSerialNumber);
        requestAgitApi(agitUrl, message);
    }

    public void sendTicketDelegateAgitAlarm(Member prevManager, Member newManager, Ticket ticket) {
        String MessageToUser = AgitTicketDelegateAlarmMessage.TICKET_DELEGATE_MESSAGE_TO_USER.format(ticket.getSerialNumber(), newManager.getName());
        requestAgitApi(ticket.getUser().getAgitUrl(), MessageToUser);

        String MessageToManager = AgitTicketDelegateAlarmMessage.TICKET_DELEGATE_MESSAGE_TO_NEW_MANAGER.format(ticket.getSerialNumber(), prevManager.getName());
        requestAgitApi(newManager.getAgitUrl(), MessageToManager);


    }

    /**
     * agitUrl로 POST 요청을 보내는 메서드 요청을 보내는데 실패하면 최대 3번까지 재시도 전송 과정에서 예외가 발생하면 즉시 중단되고 재시도하지 않음
     */
    private void requestAgitApi(String agitUrl, String message) {
        String jsonPayload = String.format("{\"text\": \"%s\"}", message);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(agitUrl))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
            .build();

        HttpClient client = HttpClient.newHttpClient();
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    return;
                }
            } catch (IOException | InterruptedException e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            attempt++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}
