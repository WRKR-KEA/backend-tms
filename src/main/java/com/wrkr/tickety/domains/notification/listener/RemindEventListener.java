package com.wrkr.tickety.domains.notification.listener;

import com.wrkr.tickety.domains.member.application.mapper.EmailMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.service.SendAgitNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.SendEmailNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.application.SendApplicationNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.kakaowork.KakaoworkMessageService;
import com.wrkr.tickety.domains.ticket.domain.event.RemindEvent;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.infrastructure.email.EmailConstants;
import com.wrkr.tickety.infrastructure.email.EmailCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@EnableAsync
public class RemindEventListener {

    private final SendAgitNotificationService sendAgitNotificationService;
    private final SendEmailNotificationService sendEmailNotificationService;
    private final SendApplicationNotificationService sendApplicationNotificationService;
    private final KakaoworkMessageService kakaoworkMessageService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRemindEvent(RemindEvent remindEvent) {
        Member member = remindEvent.receiver();
        Ticket ticket = remindEvent.ticket();

        sendAgitNotificationService.sendRemindAgitAlarm(member, ticket);

        EmailCreateRequest createRequest = EmailMapper.toEmailCreateRequest(
            ticket.getUser().getEmail(), EmailConstants.REMIND_SUBJECT, null
        );
        sendEmailNotificationService.sendRemindCreateEmail(createRequest, ticket, EmailConstants.REMIND_TYPE);

        sendApplicationNotificationService.sendRemindApplicationNotification(member, ticket);
        kakaoworkMessageService.sendRemindKakaoworkAlarm(member, ticket);
    }
}
