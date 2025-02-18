package com.wrkr.tickety.domains.notification.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.wrkr.tickety.domains.notification.application.dto.ApplicationNotificationResponse;
import com.wrkr.tickety.domains.notification.domain.model.Notification;
import com.wrkr.tickety.domains.notification.domain.service.application.NotificationGetService;
import com.wrkr.tickety.domains.notification.domain.service.application.NotificationUpdateService;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.time.temporal.TemporalUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApplicationNotificationGetUseCaseTest {

    private static final Long MEMBER_ID = 1L;

    @Mock
    private NotificationGetService notificationGetService;

    @Mock
    private NotificationUpdateService notificationUpdateService;

    @Mock
    private TemporalUnit temporalUnit;

    @InjectMocks
    private ApplicationNotificationGetUseCase applicationNotificationGetUseCase;

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = Notification.builder()
            .notificationId(1L)
            .memberId(MEMBER_ID)
            .content("New ticket assigned")
            .isRead(false)
            .build();
    }

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Nested
    @DisplayName("알림 전체 조회 API 테스트")
    class GetAllNotifications {

        @Test
        @DisplayName("성공: 사용자의 모든 알림을 정상적으로 조회한다.")
        void getAllNotifications_Success() {
            // given
            List<Notification> notifications = List.of(notification);

            given(notificationGetService.getAllNotifications(MEMBER_ID)).willReturn(notifications);

            // when
            List<ApplicationNotificationResponse> response = applicationNotificationGetUseCase.getAllNotifications(MEMBER_ID);

            // then
            assertThat(response).isNotEmpty();
            assertThat(response.size()).isEqualTo(1);
            assertThat(response.get(0).notificationId()).isEqualTo(PkCrypto.encrypt(notification.getNotificationId()));
            assertThat(response.get(0).memberId()).isEqualTo(PkCrypto.encrypt(notification.getMemberId()));
            assertThat(response.get(0).content()).isEqualTo(notification.getContent());
            assertThat(response.get(0).isRead()).isEqualTo(notification.getIsRead());
        }
    }

    @Nested
    @DisplayName("읽지 않은 알림 개수 조회 API 테스트")
    class CountMemberNotifications {

        @Test
        @DisplayName("성공: 읽지 않은 알림 개수를 정상적으로 조회한다.")
        void countMemberNotifications_Success() {
            // given
            given(notificationGetService.countByMemberIdAndIsReadFalse(MEMBER_ID)).willReturn(5L);

            // when
            long unreadCount = applicationNotificationGetUseCase.countMemberNotifications(MEMBER_ID);

            // then
            assertThat(unreadCount).isEqualTo(5L);
        }
    }
}
