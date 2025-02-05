package com.wrkr.tickety.domains.notification.domain.service;

import com.wrkr.tickety.domains.member.application.dto.request.EmailCreateRequest;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Async
public class SendEmailNotificationService {

    private final JavaMailSender javaMailSender;
    private final MapEmailTemplateService mapEmailTemplateService;

    @Async
    public void sendTicketStatusChangeEmail(EmailCreateRequest emailCreateRequest, Ticket ticket, String type) {
        String text = mapEmailTemplateService.setTicketStatusChangeContext(ticket.getStatus().getDescription(), ticket.getSerialNumber(), type);
        sendEmail(emailCreateRequest, text);
    }

    public void sendDelegateTicketManagerEmailToUser(EmailCreateRequest emailCreateRequest, Ticket ticket, Member newManager, String type) {
        String text = mapEmailTemplateService.setDelegateContext(ticket.getSerialNumber(), newManager.getNickname(), type);
        sendEmail(emailCreateRequest, text);
    }

    public void sendDelegateTicketManagerEmailToNewManager(EmailCreateRequest emailCreateRequest,
        Ticket ticket,
        Member prevManager,
        String type
    ) {
        String text = mapEmailTemplateService.setDelegateContext(ticket.getSerialNumber(), prevManager.getNickname(), type);
        sendEmail(emailCreateRequest, text);
    }

    private void sendEmail(EmailCreateRequest emailCreateRequest, String text) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailCreateRequest.to()); // 메일 수신자
            mimeMessageHelper.setSubject(emailCreateRequest.subject()); // 메일 제목
            mimeMessageHelper.setText(text, true); // 메일 본문 내용, HTML 여부
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) { // TODO: 존재하지 않은 이메일인 경우도 고려해야함
            throw new ApplicationException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
