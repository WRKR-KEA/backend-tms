package com.wrkr.tickety.domains.member.presentation;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.application.dto.response.ManagerGetAllManagerResponse;
import com.wrkr.tickety.domains.member.application.usecase.ManagerGetAllManagersUseCase;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import com.wrkr.tickety.global.utils.PkCrypto;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ManagerMemberController.class)
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
class ManagerMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ManagerGetAllManagersUseCase managerGetAllManagersUseCase;

    @MockitoBean
    private JwtUtils jwtUtils;

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    private static final Long MANAGER_ID = 1L;

    @Nested
    @DisplayName("담당자 목록 조회 API [GET /api/manager/members]")
    class getAllManagers {

        @Test
        @DisplayName("성공: 모든 담당자 조회")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw", memberId = 1L)
        void getAllManagers_Success() throws Exception {
            // Given
            ManagerGetAllManagerResponse response = ManagerGetAllManagerResponse.builder()
                .principal(
                    new ManagerGetAllManagerResponse.Managers(
                        PkCrypto.encrypt(MANAGER_ID), "profileUrl", "manager.hjw", "팀장", "manager@gachon.ac.kr", "010-1111-1111", 3L
                    )
                )
                .managers(
                    List.of(
                        new ManagerGetAllManagerResponse.Managers(
                            PkCrypto.encrypt(2L), "profileUrl2", "manager.kjw", "팀원", "manager2@gachon.ac.kr", "010-2222-2222", 5L
                        )
                    )
                )
                .build();

            doReturn(response).when(managerGetAllManagersUseCase).getAllManagers(any(Member.class));

            // When & Then
            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/manager/members")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result").exists())
                .andDo(
                    document(
                        "ManagerMemberApi/GetAllManagers/Success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                            fieldWithPath("isSuccess").description("응답 성공 여부"),
                            fieldWithPath("code").description("응답 코드"),
                            fieldWithPath("message").description("응답 메시지"),
                            fieldWithPath("result.principal.memberId").description("담당자 본인 ID"),
                            fieldWithPath("result.principal.profileUrl").description("담당자 본인 프로필 사진"),
                            fieldWithPath("result.principal.nickname").description("담당자 본인 아이디(닉네임)"),
                            fieldWithPath("result.principal.position").description("담당자 본인 직책"),
                            fieldWithPath("result.principal.email").description("담당자 본인 이메일"),
                            fieldWithPath("result.principal.phoneNumber").description("담당자 본인 전화번호"),
                            fieldWithPath("result.principal.ticketAmount").description("본인 담당 티켓 수"),
                            fieldWithPath("result.managers").description("담당자 목록"),
                            fieldWithPath("result.managers[].memberId").description("담당자 ID"),
                            fieldWithPath("result.managers[].profileUrl").description("담당자 프로필 사진"),
                            fieldWithPath("result.managers[].nickname").description("담당자 아이디(닉네임)"),
                            fieldWithPath("result.managers[].position").description("담당자 직책"),
                            fieldWithPath("result.managers[].email").description("담당자 이메일"),
                            fieldWithPath("result.managers[].phoneNumber").description("담당자 전화번호"),
                            fieldWithPath("result.managers[].ticketAmount").description("담당 티켓 수")
                        )
                    )
                );
        }

        @Test
        @DisplayName("실패: 인증 정보 없음")
        void getAllManagers_Fail_Unauthorized() throws Exception {
            // When & Then
            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/manager/members")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(
                    document(
                        "ManagerMemberApi/GetAllManagers/Failure/Case1",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                    )
                );
        }

        @Test
        @DisplayName("실패: 내부 서버 오류")
        @WithMockCustomUser(username = "manager", role = Role.MANAGER, nickname = "manager.hjw", memberId = 1L)
        void getAllManagers_Fail_InternalError() throws Exception {
            // Given
            doThrow(new ApplicationException(CommonErrorCode.INTERNAL_SERVER_ERROR))
                .when(managerGetAllManagersUseCase).getAllManagers(any(Member.class));

            // When & Then
            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/manager/members")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.INTERNAL_SERVER_ERROR.getCustomCode()))
                .andExpect(jsonPath("$.message").value(CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage()))
                .andDo(
                    document(
                        "ManagerMemberApi/GetAllManagers/Failure/Case2",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                    )
                );
        }
    }
}
