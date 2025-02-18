package com.wrkr.tickety.domains.notification.presentation;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.notification.application.dto.ApplicationNotificationResponse;
import com.wrkr.tickety.domains.notification.application.usecase.ApplicationNotificationGetUseCase;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ApplicationNotificationController.class)
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
class ApplicationNotificationControllerTest {

    private static final Long MEMBER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ApplicationNotificationGetUseCase applicationNotificationGetUseCase;

    @MockitoBean
    private JwtUtils jwtUtils;

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Nested
    @DisplayName("알림 전체 조회 API [GET /api/user/notifications]")
    class GetAllNotifications {

        @Test
        @DisplayName("성공: 사용자의 모든 알림을 정상적으로 조회한다.")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "test.user", memberId = 1L)
        void getAllNotifications_Success() throws Exception {
            // given
            ApplicationNotificationResponse notificationResponse = ApplicationNotificationResponse.builder()
                .notificationId(PkCrypto.encrypt("1"))
                .memberId(PkCrypto.encrypt(String.valueOf(MEMBER_ID)))
                .profileImage("https://example.com/profile.jpg")
                .content("새로운 알림이 도착했습니다.")
                .type(null)
                .isRead(false)
                .timeAgo("10분 전")
                .createdAt(LocalDateTime.now())
                .build();

            List<ApplicationNotificationResponse> responses = List.of(notificationResponse);
            doReturn(responses).when(applicationNotificationGetUseCase).getAllNotifications(anyLong());

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders
                    .get("/api/user/notifications")
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result[0].content").value("새로운 알림이 도착했습니다."))
                .andDo(document(
                    "ApplicationNotificationApi/GetAllNotifications/Success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("isSuccess").description("응답 성공 여부"),
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("result").description("응답 결과"),
                        fieldWithPath("result[].notificationId").description("알림 PK"),
                        fieldWithPath("result[].memberId").description("회원 PK"),
                        fieldWithPath("result[].profileImage").description("프로필 이미지 url"),
                        fieldWithPath("result[].content").description("알림 내용"),
                        fieldWithPath("result[].type").description("알림 구분 (TICKET | COMMENT | REMIND)"),
                        fieldWithPath("result[].isRead").description("읽음 여부 (true | false)"),
                        fieldWithPath("result[].timeAgo").description("경과 시간"),
                        fieldWithPath("result[].createdAt").description("발송 일시")
                    )
                ));
        }

        @Test
        @DisplayName("실패: 인증되지 않은 사용자 접근 시 401 Unauthorized를 반환한다.")
        void getAllNotifications_Unauthorized() throws Exception {
            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders
                    .get("/api/user/notifications")
                    .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(document(
                    "ApplicationNotificationApi/GetAllNotifications/Failure/Unauthorized",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint())
                ));
        }
    }

    @Nested
    @DisplayName("읽지 않은 알림 개수 조회 API [GET /api/user/notifications/count]")
    class CountMemberNotifications {

        @Test
        @DisplayName("성공: 읽지 않은 알림 개수를 정상적으로 조회한다.")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "test.user", memberId = 1L)
        void countMemberNotifications_Success() throws Exception {
            // given
            doReturn(5L).when(applicationNotificationGetUseCase).countMemberNotifications(anyLong());

            // when & then
            mockMvc.perform(get("/api/user/notifications/count")
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result").value(5))
                .andDo(document(
                    "ApplicationNotificationApi/CountMemberNotifications/Success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("isSuccess").description("응답 성공 여부"),
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("result").description("읽지 않은 알림 개수")
                    )
                ));
        }

        @Test
        @DisplayName("실패: 인증되지 않은 사용자 접근 시 401 Unauthorized를 반환한다.")
        void countMemberNotifications_Unauthorized() throws Exception {
            // when & then
            mockMvc.perform(get("/api/user/notifications/count")
                    .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(document(
                    "ApplicationNotificationApi/CountMemberNotifications/Failure/Unauthorized",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint())
                ));
        }
    }
}
