package com.wrkr.tickety.domains.notification.listener;

import static com.wrkr.tickety.domains.notification.application.mapper.NotificationMapper.toNotification;

import com.wrkr.tickety.domains.member.application.mapper.EmailMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.notification.domain.constant.NotificationType;
import com.wrkr.tickety.domains.notification.domain.constant.application.Remind;
import com.wrkr.tickety.domains.notification.domain.service.NotificationRunner;
import com.wrkr.tickety.domains.notification.domain.service.SendAgitNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.SendEmailNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.application.NotificationSaveService;
import com.wrkr.tickety.domains.notification.domain.service.application.SendApplicationNotificationService;
import com.wrkr.tickety.domains.notification.domain.service.kakaowork.KakaoworkMessageService;
import com.wrkr.tickety.domains.ticket.domain.event.RemindEvent;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.infrastructure.email.EmailConstants;
import com.wrkr.tickety.infrastructure.email.EmailCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RemindEventListener {

    private final SendAgitNotificationService sendAgitNotificationService;
    private final SendEmailNotificationService sendEmailNotificationService;
    private final SendApplicationNotificationService sendApplicationNotificationService;
    private final KakaoworkMessageService kakaoworkMessageService;
    private final NotificationSaveService notificationSaveService;
    private final NotificationRunner notificationRunner;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRemindEvent(RemindEvent remindEvent) {
        Member member = remindEvent.member();
        Ticket ticket = remindEvent.ticket();
        String message = Remind.REMIND_TICKET.format(ticket.getSerialNumber());

        notificationRunner.run(member, () -> sendAgitNotificationService.sendRemindAgitAlarm(member, ticket));

        notificationRunner.run(member, () -> {
            EmailCreateRequest createRequest = EmailMapper.toEmailCreateRequest(
                ticket.getUser().getEmail(), EmailConstants.REMIND_SUBJECT, null
            );
            sendEmailNotificationService.sendRemindCreateEmail(createRequest, ticket, EmailConstants.REMIND_TYPE);
        });

        notificationRunner.run(member, () -> sendApplicationNotificationService.sendRemindApplicationNotification(member, ticket));

        notificationRunner.run(member, () -> kakaoworkMessageService.sendRemindKakaoworkAlarm(member, ticket));

        notificationSaveService.save(toNotification(member.getMemberId(), member.getProfileImage(), NotificationType.REMIND, message));
    }
}
