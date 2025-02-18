package com.wrkr.tickety.domains.notification.domain.service;

import com.wrkr.tickety.domains.member.application.mapper.EmailMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import com.wrkr.tickety.infrastructure.email.EmailConstants;
import com.wrkr.tickety.infrastructure.email.EmailCreateRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SendEmailNotificationService {

    private final JavaMailSender javaMailSender;
    private final MapEmailTemplateService mapEmailTemplateService;

    @Async
    public void sendTicketStatusChangeEmail(Member receiver, Ticket ticket, String type) {
        EmailCreateRequest emailCreateRequest = EmailMapper.toEmailCreateRequest(receiver.getEmail(), EmailConstants.TICKET_STATUS_CHANGE_SUBJECT, null);
        String text = mapEmailTemplateService.setTicketStatusChangeContext(ticket.getStatus().getDescription(), ticket.getSerialNumber(), type);
        sendEmail(emailCreateRequest, text);
    }

    @Async
    public void sendDelegateTicketManagerEmailToUser(Member receiver, Ticket ticket, Member newManager, String type) {
        EmailCreateRequest emailCreateRequest = EmailMapper.toEmailCreateRequest(
            receiver.getEmail(), EmailConstants.TICKET_DELEGATE_SUBJECT, null
        );
        String text = mapEmailTemplateService.setDelegateContext(ticket.getSerialNumber(), newManager.getNickname(), type);
        sendEmail(emailCreateRequest, text);
    }

    @Async
    public void sendDelegateTicketManagerEmailToNewManager(Member receiver, Ticket ticket, Member prevManager, String type) {
        EmailCreateRequest emailCreateRequest = EmailMapper.toEmailCreateRequest(
            receiver.getEmail(), EmailConstants.TICKET_DELEGATE_SUBJECT, null
        );
        String text = mapEmailTemplateService.setDelegateContext(ticket.getSerialNumber(), prevManager.getNickname(), type);
        sendEmail(emailCreateRequest, text);
    }

    @Async
    public void sendCommentCreateEmail(Member receiver, Ticket ticket, String type) {
        EmailCreateRequest emailCreateRequest = EmailCreateRequest.builder()
            .to(receiver.getEmail())
            .subject(EmailConstants.TICKET_COMMENT_SUBJECT)
            .build();
        String text = mapEmailTemplateService.setCommentCreateContext(ticket.getSerialNumber(), type);
        sendEmail(emailCreateRequest, text);
    }

    @Async
    public void sendRemindCreateEmail(Member receiver, Ticket ticket, String type) {
        EmailCreateRequest emailCreateRequest = EmailMapper.toEmailCreateRequest(
            receiver.getEmail(), EmailConstants.REMIND_SUBJECT, null
        );
        String text = mapEmailTemplateService.setRemindContext(ticket.getSerialNumber(), type);
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
