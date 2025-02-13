package com.wrkr.tickety.domains.member.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.docs.RestDocsSupport;
import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.usecase.ExcelExampleCreateUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberCreateFromExcelUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberCreateUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberInfoGetUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberInfoSearchUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberInfoUpdateUseCase;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.excel.ExcelUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = AdminMemberController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class AdminMemberControllerTest extends RestDocsSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberCreateFromExcelUseCase memberCreateFromExcelUseCase;

    @MockitoBean
    private MemberCreateUseCase memberCreateUseCase;

    @MockitoBean
    private MemberInfoUpdateUseCase memberInfoUpdateUseCase;

    @MockitoBean
    private MemberInfoGetUseCase memberInfoGetUseCase;

    @MockitoBean
    private MemberInfoSearchUseCase memberInfoSearchUseCase;

    @MockitoBean
    private ExcelExampleCreateUseCase excelExampleCreateUseCase;

    @MockitoBean
    private ExcelUtil excelUtil;

    @MockitoBean
    private JwtUtils jwtUtils;

    private MockedStatic<PkCrypto> pkCrpytoMockedStatic;

    @BeforeEach
    public void setUp() {
        pkCrpytoMockedStatic = mockStatic(PkCrypto.class);
    }

    @AfterEach
    public void tearDown() {
        pkCrpytoMockedStatic.close();
    }

/*    @BeforeEach
    public void setup() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }*/

    @Override
    protected Object initController() {
        return new AdminMemberController(
            memberCreateFromExcelUseCase,
            memberCreateUseCase,
            memberInfoUpdateUseCase,
            memberInfoGetUseCase,
            memberInfoSearchUseCase,
            excelExampleCreateUseCase,
            excelUtil
        );
    }


    @Nested
    @DisplayName("회원 등록 API 테스트")
    class CreateMemberTest {

        @Test
        @DisplayName("회원 등록에 성공한다.")
        @WithMockCustomUser(username = "admin", role = Role.ADMIN, nickname = "admin.ad", memberId = 1L)
        void createMemberSuccess() throws Exception {
            // given
            Long memberId = 2L; // 예시로 사용할 memberId
            String encryptedMemberId = "encryptedMemberId";
            String nickname = "test.user";
            String email = "test@gachon.ac.kr";
            String name = "TestUser";
            String department = "BE team1";
            String position = "BE Developer";
            String phone = "010-1234-5678";
            Role role = Role.USER;
            String agitUrl = "https://www.gachon.ac.kr";

            MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .department(department)
                .position(position)
                .phone(phone)
                .role(role)
                .agitUrl(agitUrl)
                .build();

            MockMultipartFile requestJson = new MockMultipartFile(
                "request",
                "request.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(memberCreateRequest)
            );

            MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
            );

            when(memberCreateUseCase.createMember(any(), any())).thenReturn(
                MemberPkResponse.builder()
                    .memberId(encryptedMemberId)
                    .build()
            );

            // when & then
            ResultActions perform = mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/admin/members")
                .file(requestJson)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .with(csrf()));

            perform
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.isSuccess").value(true),
                    jsonPath("$.code").value("SUCCESS"),
                    jsonPath("$.message").value("성공"),
                    jsonPath("$.result.memberId").value(encryptedMemberId)
                );

            perform.andDo(document("createMember",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("관리자 - 회원 등록 API")
                        .summary("회원을 등록 시 이메일로 임시 비밀번호를 발급합니다.")
                        .description(""),
                    requestFields(
                        fieldWithPath("memberId").type(JsonFieldType.STRING).description("회원 PK")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부")
                    )
                )
            ));
        }

        /*@Test
        @WithMockCustomUser() // 관리자 권한으로 테스트
        @DisplayName("회원 등록에 실패한다. (유효하지 않은 이메일 형식)")
        void createMember_Failure_InvalidEmail() throws Exception {
            // given
            MemberCreateRequest memberCreateRequest = new MemberCreateRequest();
            memberCreateRequest.setEmail("invalid-email"); // 유효하지 않은 이메일
            memberCreateRequest.setName("Test User");
            memberCreateRequest.setNickname("testuser");

            MockMultipartFile requestFile = new MockMultipartFile(
                "request",
                "request.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(memberCreateRequest)
            );

            // when & then
            mockMvc.perform(multipart("/api/members")
                    .file(requestFile)
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest()); // 응답 상태 코드 검증
        }*/
    }
}