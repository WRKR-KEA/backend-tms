package com.wrkr.tickety.domains.notification.domain.service.application;

import com.wrkr.tickety.domains.notification.persistence.adapter.NotificationPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationUpdateService {

    private final NotificationPersistenceAdapter notificationPersistenceAdapter;

    public void updateAllIsReadByMemberId(Long memberId) {
        notificationPersistenceAdapter.updateAllIsReadTrueByMemberId(memberId);
    }
}
