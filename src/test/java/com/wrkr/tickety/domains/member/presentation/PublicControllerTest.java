package com.wrkr.tickety.domains.member.presentation;

import static com.wrkr.tickety.domains.auth.exception.AuthErrorCode.INVALID_VERIFICATION_CODE;
import static com.wrkr.tickety.domains.member.domain.constant.Role.MANAGER;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_NICKNAME;
import static com.wrkr.tickety.global.response.code.SuccessCode.SUCCESS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.application.dto.request.PasswordReissueRequest;
import com.wrkr.tickety.domains.member.application.dto.request.VerificationCodeRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.usecase.PasswordReissueUseCase;
import com.wrkr.tickety.domains.member.application.usecase.VerificationCodeCreateUseCase;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.exception.ApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(PublicController.class)
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
public class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VerificationCodeCreateUseCase verificationCodeCreateUseCase;

    @MockitoBean
    private PasswordReissueUseCase passwordReissueUseCase;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Nested
    @DisplayName("인증 코드 발급 API 테스트")
    class CreateVerificationCode {

        @Test
        @DisplayName("유효한 닉네임으로 요청 시 인증 코드가 정상 발급된다.")
        @WithMockCustomUser(username = "manager", role = MANAGER, nickname = "manager.thama", memberId = 2L)
        void createVerificationCodeSuccess() throws Exception {
            // given
            String nickname = "validNickname";
            String encryptedMemberId = "encryptedMemberId";
            MemberPkResponse response = new MemberPkResponse(encryptedMemberId);
            when(verificationCodeCreateUseCase.createVerificationCode(anyString())).thenReturn(response);

            String requestBody = objectMapper.writeValueAsString(new VerificationCodeRequest(nickname));

            // when
            ResultActions requestBuilder = mockMvc.perform(post("/api/members/password/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(csrf()));

            // then
            requestBuilder.andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.isSuccess").value(true),
                    jsonPath("$.code").value(SUCCESS.getCustomCode()),
                    jsonPath("$.message").value(SUCCESS.getMessage()),
                    jsonPath("$.result.memberId").value(encryptedMemberId)
                )
                .andDo(document(
                    "Public/createVerificationCode/Success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("nickname").description("사용자의 닉네임")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").description("응답 성공 여부"),
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("result.memberId").description("암호화된 회원 PK")
                    )
                ));
        }

        @Test
        @DisplayName("존재하지 않는 회원 닉네임으로 요청 시 INVALID_NICKNAME 예외가 발생한다.")
        @WithMockCustomUser(username = "manager", role = MANAGER, nickname = "manager.thama", memberId = 2L)
        void createVerificationCodeMemberNotFound() throws Exception {
            // given
            String nickname = "nonExistentNickname";
            doThrow(ApplicationException.from(INVALID_NICKNAME)).when(verificationCodeCreateUseCase)
                .createVerificationCode(anyString());

            String requestBody = objectMapper.writeValueAsString(new VerificationCodeRequest(nickname));

            // when
            ResultActions requestBuilder = mockMvc.perform(post("/api/members/password/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(csrf()));

            // then
            requestBuilder.andDo(print())
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").value(INVALID_NICKNAME.getCustomCode()),
                    jsonPath("$.message").value(INVALID_NICKNAME.getMessage())
                )
                .andDo(document(
                    "Public/createVerificationCode/Failure/InvalidNickname",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("nickname").description("사용자의 닉네임")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").description("응답 성공 여부"),
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지")
                    )
                ));
        }

        @Test
        @DisplayName("닉네임(아이디) 형식이 올바르지 않은 경우 INVALID_NICKNAME 예외를 발생시킨다.")
        @WithMockCustomUser(username = "manager", role = MANAGER, nickname = "manager.thama", memberId = 2L)
        void createVerificationCodeNicknameFormatIsInvalid() throws Exception {
            // given
            String nickname = "invalid_nickname_format";
            doThrow(ApplicationException.from(INVALID_NICKNAME)).when(verificationCodeCreateUseCase)
                .createVerificationCode(anyString());

            String requestBody = objectMapper.writeValueAsString(new VerificationCodeRequest(nickname));

            // when
            ResultActions requestBuilder = mockMvc.perform(post("/api/members/password/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(csrf()));

            // then
            requestBuilder.andDo(print())
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").value(INVALID_NICKNAME.getCustomCode()),
                    jsonPath("$.message").value(INVALID_NICKNAME.getMessage())
                )
                .andDo(document(
                    "Public/createVerificationCode/Failure/InvalidNicknameFormat",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("nickname").description("사용자의 닉네임")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").description("응답 성공 여부"),
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지")
                    )
                ));
        }
    }

    @Nested
    @DisplayName("비밀번호 재발급 API 테스트")
    class RegeneratePassword {

        @Test
        @DisplayName("유효한 인증 코드와 회원 ID로 비밀번호 재발급에 성공한다.")
        @WithMockCustomUser(username = "manager", role = MANAGER, nickname = "manager.thama", memberId = 2L)
        void regeneratePasswordSuccess() throws Exception {
            // given
            String encryptedMemberId = "encryptedMemberId";
            String verificationCode = "validVerificationCode";
            MemberPkResponse response = new MemberPkResponse(encryptedMemberId);
            when(passwordReissueUseCase.reissuePassword(encryptedMemberId, verificationCode)).thenReturn(response);

            String requestBody = objectMapper.writeValueAsString(new PasswordReissueRequest(encryptedMemberId, verificationCode));

            // when
            ResultActions requestBuilder = mockMvc.perform(patch("/api/members/password/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(csrf()));

            // then
            requestBuilder.andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.isSuccess").value(true),
                    jsonPath("$.code").value(SUCCESS.getCustomCode()),
                    jsonPath("$.message").value(SUCCESS.getMessage()),
                    jsonPath("$.result.memberId").value(encryptedMemberId)
                )
                .andDo(document(
                    "Public/regeneratePassword/Success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("memberId").description("암호화된 회원 ID"),
                        fieldWithPath("verificationCode").description("인증 코드")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").description("응답 성공 여부"),
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("result.memberId").description("암호화된 회원 ID")
                    )
                ));
        }

        @Test
        @DisplayName("잘못된 인증 코드로 비밀번호 재발급 요청 시 INVALID_VERIFICATION_CODE 예외가 발생한다.")
        @WithMockCustomUser(username = "manager", role = MANAGER, nickname = "manager.thama", memberId = 2L)
        void regeneratePasswordInvalidVerificationCode() throws Exception {
            // given
            String encryptedMemberId = "encryptedMemberId";
            String invalidVerificationCode = "invalidVerificationCode";
            doThrow(ApplicationException.from(INVALID_VERIFICATION_CODE)).when(passwordReissueUseCase)
                .reissuePassword(encryptedMemberId, invalidVerificationCode);

            String requestBody = objectMapper.writeValueAsString(new PasswordReissueRequest(encryptedMemberId, invalidVerificationCode));

            // when
            ResultActions requestBuilder = mockMvc.perform(patch("/api/members/password/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(csrf()));

            // then
            requestBuilder.andDo(print())
                .andExpectAll(
                    status().isUnauthorized(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").value(INVALID_VERIFICATION_CODE.getCustomCode()),
                    jsonPath("$.message").value(INVALID_VERIFICATION_CODE.getMessage())
                )
                .andDo(document(
                    "Public/regeneratePassword/Failure/InvalidVerificationCode",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("memberId").description("암호화된 회원 ID"),
                        fieldWithPath("verificationCode").description("인증 코드")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").description("응답 성공 여부"),
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지")
                    )
                ));
        }

        @Test
        @DisplayName("존재하지 않는 회원으로 비밀번호 재발급 요청 시 INVALID_VERIFICATION_CODE 예외가 발생한다.")
        @WithMockCustomUser(username = "manager", role = MANAGER, nickname = "manager.thama", memberId = 2L)
        void regeneratePasswordMemberNotFound() throws Exception {
            // given
            String encryptedMemberId = "nonExistentEncryptedMemberId";
            String verificationCode = "validVerificationCode";
            doThrow(ApplicationException.from(INVALID_VERIFICATION_CODE)).when(passwordReissueUseCase)
                .reissuePassword(encryptedMemberId, verificationCode);

            String requestBody = objectMapper.writeValueAsString(new PasswordReissueRequest(encryptedMemberId, verificationCode));

            // when
            ResultActions requestBuilder = mockMvc.perform(patch("/api/members/password/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(csrf()));

            // then
            requestBuilder.andDo(print())
                .andExpectAll(
                    status().isUnauthorized(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").value(INVALID_VERIFICATION_CODE.getCustomCode()),
                    jsonPath("$.message").value(INVALID_VERIFICATION_CODE.getMessage())
                )
                .andDo(document(
                    "Public/regeneratePassword/Failure/NotExistMember",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("memberId").description("암호화된 회원 ID"),
                        fieldWithPath("verificationCode").description("인증 코드")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").description("응답 성공 여부"),
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지")
                    )
                ));
        }
    }
}
