package com.wrkr.tickety.domains.member.presentation;

import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.application.dto.request.MyPageInfoUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MyPageInfoResponse;
import com.wrkr.tickety.domains.member.application.usecase.MyPageInfoGetUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MyPageInfoUpdateUseCase;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MyPageController.class)
@AutoConfigureMockMvc
class MyPageControllerTest {

    private static final Long USER_ID = 1L;

    private Member user;
    private MyPageInfoResponse myPageInfoResponse;
    private MyPageInfoUpdateRequest validRequest;
    private MyPageInfoUpdateRequest duplicateEmailRequest;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MyPageInfoGetUseCase myPageInfoGetUseCase;

    @MockitoBean
    private MyPageInfoUpdateUseCase myPageInfoUpdateUseCase;

    @MockitoBean
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        user = Member.builder()
            .memberId(USER_ID)
            .password("password")
            .nickname("사용자")
            .email("user@gachon.ac.kr")
            .phone("010-1234-5678")
            .name("사용자")
            .role(Role.USER)
            .isDeleted(false)
            .build();

        myPageInfoResponse = MyPageInfoResponse.builder()
            .memberId(PkCrypto.encrypt(USER_ID))
            .nickname(user.getNickname())
            .name(user.getName())
            .phone(user.getPhone())
            .email(user.getEmail())
            .position("팀장")
            .profileImage("http://image.com")
            .role(user.getRole().name())
            .agitUrl("http://agit.com")
            .department("개발팀")
            .agitNotification(true)
            .emailNotification(true)
            .serviceNotification(true)
            .kakaoworkNotification(true)
            .build();

        validRequest = MyPageInfoUpdateRequest.builder()
            .name("newName")
            .phone("010-9876-5432")
            .email("newemail@gachon.ac.kr")
            .department("newDepartment")
            .position("newPosition")
            .agitUrl("newAgitUrl")
            .agitNotification(true)
            .emailNotification(true)
            .serviceNotification(true)
            .kakaoworkNotification(true)
            .build();

        duplicateEmailRequest = MyPageInfoUpdateRequest.builder()
            .name("newName")
            .phone("010-9876-5432")
            .email("duplicate@gachon.ac.kr") // 중복 이메일 요청
            .department("newDepartment")
            .position("newPosition")
            .agitUrl("newAgitUrl")
            .agitNotification(true)
            .emailNotification(true)
            .serviceNotification(true)
            .kakaoworkNotification(true)
            .build();
    }

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Nested
    @DisplayName("마이페이지 회원 정보 조회 API 테스트")
    class GetMyPageInfoTest {

        @Test
        @DisplayName("✅ 성공: 회원 정보 조회")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "manager.psw", memberId = 1L)
        void getMyPageInfo_Success() throws Exception {
            // given
            given(myPageInfoGetUseCase.getMyPageInfo(USER_ID)).willReturn(myPageInfoResponse);

            // when & then
            mockMvc.perform(get("/api/user/my-page"))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("❌ 실패: 회원을 찾을 수 없음")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "manager.psw", memberId = 1L)
        void getMyPageInfo_NotFound() throws Exception {
            // given
            given(myPageInfoGetUseCase.getMyPageInfo(USER_ID))
                .willThrow(ApplicationException.from(MemberErrorCode.MEMBER_NOT_FOUND));

            // when & then
            mockMvc.perform(get("/api/user/my-page"))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("❌ 실패: 삭제된 회원은 조회 할 수 없음")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "manager.psw", memberId = 1L)
        void getMyPageInfo_Deleted() throws Exception {
            // given
            user.modifyIsDeleted(true);
            given(myPageInfoGetUseCase.getMyPageInfo(USER_ID))
                .willThrow(ApplicationException.from(MemberErrorCode.DELETED_MEMBER));

            // when & then
            mockMvc.perform(get("/api/user/my-page"))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("마이페이지 회원 정보 수정 API 테스트")
    class UpdateMyPageInfoTest {

        @Test
        @DisplayName("✅ 성공: 회원 정보 수정")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "manager.psw", memberId = 1L)
        void updateMyPageInfo_Success() throws Exception {
            // given
            MemberPkResponse response = MemberPkResponse.builder()
                .memberId(PkCrypto.encrypt(USER_ID))
                .build();

            given(myPageInfoUpdateUseCase.updateMyPageInfo(USER_ID, validRequest))
                .willReturn(response);

            // when & then
            mockMvc.perform(patch("/api/user/my-page")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("❌ 실패: 회원을 찾을 수 없음")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "manager.psw", memberId = 1L)
        void updateMyPageInfo_NotFound() throws Exception {
            // given
            doThrow(ApplicationException.from(MemberErrorCode.MEMBER_NOT_FOUND))
                .when(myPageInfoUpdateUseCase)
                .updateMyPageInfo(USER_ID, validRequest);

            // when & then
            mockMvc.perform(patch("/api/user/my-page")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("❌ 실패: 중복 이메일로 인한 수정 실패")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "manager.psw", memberId = 1L)
        void updateMyPageInfo_DuplicateEmail() throws Exception {
            // given
            doThrow(ApplicationException.from(MemberErrorCode.ALREADY_EXIST_EMAIL))
                .when(myPageInfoUpdateUseCase)
                .updateMyPageInfo(USER_ID, duplicateEmailRequest);

            // when & then
            mockMvc.perform(patch("/api/user/my-page")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(duplicateEmailRequest)))
                .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("❌ 실패: 삭제된 회원은 수정 할 수 없음")
        @WithMockCustomUser(username = "user", role = Role.USER, nickname = "manager.psw", memberId = 1L)
        void updateMyPageInfo_Deleted() throws Exception {
            // given
            user.modifyIsDeleted(true);
            given(myPageInfoUpdateUseCase.updateMyPageInfo(USER_ID, validRequest))
                .willThrow(ApplicationException.from(MemberErrorCode.DELETED_MEMBER));

            // when & then
            mockMvc.perform(patch("/api/user/my-page")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
        }
    }
}
