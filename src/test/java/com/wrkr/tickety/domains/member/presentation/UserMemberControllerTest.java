package com.wrkr.tickety.domains.member.presentation;

import static com.wrkr.tickety.domains.member.domain.constant.Role.MANAGER;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_PASSWORD_FORMAT;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.UNMATCHED_PASSWORD;
import static com.wrkr.tickety.global.response.code.CommonErrorCode.BAD_REQUEST;
import static com.wrkr.tickety.global.response.code.SuccessCode.SUCCESS;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.application.dto.request.PasswordUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.usecase.PasswordUpdateUseCase;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(UserMemberController.class)
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
class UserMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PasswordUpdateUseCase passwordUpdateUseCase;

    @MockitoBean
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @ParameterizedTest
    @DisplayName("비밀번호 형식(특수문자, 대문자, 숫자를 포함하는 8~12자 비밀번호)이 올바르면 비밀번호 재설정에 성공한다.")
    @WithMockCustomUser(username = "manager", role = MANAGER, nickname = "manager.thama", memberId = 2L)
    @CsvSource({
        "Abc123!@",
        "Abcdef123!@#"
    })
    void updatePasswordValidFormat(String password) throws Exception {
        // given
        final MemberPkResponse response = new MemberPkResponse(PkCrypto.encrypt(2L));
        doReturn(response).when(passwordUpdateUseCase).updatePassword(anyLong(), anyString(), anyString());

        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
            .password(password)
            .confirmPassword(password)
            .build();

        String requestBody = objectMapper.writeValueAsString(request);

        // when
        ResultActions requestBuilder = mockMvc.perform(patch("/api/user/members/password")
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
                jsonPath("$.result.memberId").value(response.memberId())
            )
            .andDo(document(
                "UserMember/updatePassword/Success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("password").description("새 비밀번호"),
                    fieldWithPath("confirmPassword").description("비밀번호 확인")
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
    @DisplayName("비밀번호가 일치하지 않을 경우 UNMATCHED_PASSWORD 예외가 발생한다.")
    @WithMockCustomUser(username = "manager", role = MANAGER, nickname = "manager.thama", memberId = 2L)
    void updatePasswordUnmatchedPassword() throws Exception {
        // given
        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
            .password("newPwd123!")
            .confirmPassword("diffPwd123!")
            .build();

        String requestBody = objectMapper.writeValueAsString(request);

        doThrow(ApplicationException.from(UNMATCHED_PASSWORD)).when(passwordUpdateUseCase)
            .updatePassword(anyLong(), anyString(), anyString());

        // when
        ResultActions requestBuilder = mockMvc.perform(patch("/api/user/members/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .with(csrf()));

        // then
        requestBuilder.andDo(print())
            .andExpectAll(
                status().isBadRequest(),
                jsonPath("$.isSuccess").value(false),
                jsonPath("$.code").value(UNMATCHED_PASSWORD.getCustomCode()),
                jsonPath("$.message").value(UNMATCHED_PASSWORD.getMessage())
            )
            .andDo(document(
                "UserMember/updatePassword/Failure/UnmatchedPassword",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("password").description("새 비밀번호"),
                    fieldWithPath("confirmPassword").description("비밀번호 확인 (다른 값)")
                ),
                responseFields(
                    fieldWithPath("isSuccess").description("응답 성공 여부"),
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지")
                )
            ));
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID로 요청 시 MEMBER_NOT_FOUND 예외가 발생한다.")
    @WithMockCustomUser(username = "manager", role = MANAGER, nickname = "manager.thama", memberId = 2L)
    void updatePasswordMemberNotFound() throws Exception {
        // given
        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
            .password("newPwd123!")
            .confirmPassword("newPwd123!")
            .build();

        String requestBody = objectMapper.writeValueAsString(request);

        doThrow(ApplicationException.from(MEMBER_NOT_FOUND)).when(passwordUpdateUseCase)
            .updatePassword(anyLong(), anyString(), anyString());

        // when
        ResultActions requestBuilder = mockMvc.perform(patch("/api/user/members/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .with(csrf()));

        // then
        requestBuilder.andDo(print())
            .andExpectAll(
                status().isNotFound(),
                jsonPath("$.isSuccess").value(false),
                jsonPath("$.code").value(MEMBER_NOT_FOUND.getCustomCode()),
                jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage())
            )
            .andDo(document(
                "UserMember/updatePassword/Failure/MemberNotFound",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("password").description("새 비밀번호"),
                    fieldWithPath("confirmPassword").description("비밀번호 확인")
                ),
                responseFields(
                    fieldWithPath("isSuccess").description("응답 성공 여부"),
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지")
                )
            ));
    }

    @ParameterizedTest
    @DisplayName("비밀번호 형식이 올바르지 않으면 BAD_REQUEST 예외가 발생한다.")
    @WithMockCustomUser(username = "manager", role = MANAGER, nickname = "manager.thama", memberId = 2L)
    @CsvSource({
        "Abc12!",
        "Abcdef1234!@#",
        "Abcdefg!",
        "abcdef123!",
        "Abcdef123"
    })
    void updatePasswordInvalidPasswordFormat(String password) throws Exception {
        // given
        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
            .password(password)
            .confirmPassword(password)
            .build();

        String requestBody = objectMapper.writeValueAsString(request);

        // when
        ResultActions requestBuilder = mockMvc.perform(patch("/api/user/members/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .with(csrf()));

        // then
        requestBuilder.andDo(print())
            .andExpectAll(
                status().isBadRequest(),
                jsonPath("$.isSuccess").value(false),
                jsonPath("$.code").value(BAD_REQUEST.getCustomCode()),
                jsonPath("$.message").value(BAD_REQUEST.getMessage()),
                jsonPath("$.result.password").value(INVALID_PASSWORD_FORMAT.getMessage()),
                jsonPath("$.result.confirmPassword").value(INVALID_PASSWORD_FORMAT.getMessage())
            )
            .andDo(document(
                "UserMember/updatePassword/Failure/InvalidPasswordFormat",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("password").description("새 비밀번호"),
                    fieldWithPath("confirmPassword").description("비밀번호 확인")
                ),
                responseFields(
                    fieldWithPath("isSuccess").description("응답 성공 여부"),
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("result.confirmPassword").description("비밀번호 확인 필드에 대한 오류 메시지"),
                    fieldWithPath("result.password").description("비밀번호 입력 필드에 대한 오류 메시지")
                )
            ));
    }
}
