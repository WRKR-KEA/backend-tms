package com.wrkr.tickety.domains.member.presentation;

import static com.wrkr.tickety.common.fixture.member.UserFixture.USER_J;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.usecase.ExcelExampleCreateUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberCreateFromExcelUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberCreateUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberInfoGetUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberInfoSearchUseCase;
import com.wrkr.tickety.domains.member.application.usecase.MemberInfoUpdateUseCase;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.response.code.SuccessCode;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.excel.ExcelUtil;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(controllers = AdminMemberController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class AdminMemberControllerTest {

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

    @MockitoBean
    private PkCrypto pkCrypto;

    @BeforeEach
    public void setUp() {
        pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }


    @Nested
    @DisplayName("회원 등록 API 테스트")
    class CreateMemberTest {

        @Test
        @DisplayName("회원 등록에 성공한다.")
        @WithMockCustomUser(username = "admin", role = Role.ADMIN, nickname = "admin.ad", memberId = 1L)
        void createMemberSuccess() throws Exception {
            // given
            Member member = USER_J.toMember();

            MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .department(member.getDepartment())
                .position(member.getPosition())
                .phone(member.getPhone())
                .role(member.getRole())
                .agitUrl(member.getAgitUrl())
                .build();

            String encryptedMemberId = pkCrypto.encryptValue(member.getMemberId());

            String content = objectMapper.writeValueAsString(memberCreateRequest);
            MockMultipartFile requestJson = new MockMultipartFile(
                "request",
                "request",
                MediaType.APPLICATION_JSON_VALUE,
                content.getBytes(StandardCharsets.UTF_8)
            );

            MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "profileImage.png",
                MediaType.IMAGE_PNG_VALUE,
                "profileImage.png".getBytes()
            );

            when(memberCreateUseCase.createMember(any(MemberCreateRequest.class), any(MultipartFile.class))).thenReturn(
                MemberPkResponse.builder()
                    .memberId(encryptedMemberId)
                    .build()
            );

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                .multipart("/api/admin/members")
                .file(requestJson)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf());

            // then
            mockMvc.perform(requestBuilder)
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.isSuccess").value(true),
                    jsonPath("$.code").value(SuccessCode.SUCCESS.getCustomCode()),
                    jsonPath("$.message").value(SuccessCode.SUCCESS.getMessage()),
                    jsonPath("$.result.memberId").value(encryptedMemberId)
                )
                .andDo(
                    document(
                        "AdminMember/Request/Success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                            fieldWithPath("request.email").type(JsonFieldType.STRING).description("회원 이메일 주소"),
                            fieldWithPath("request.name").type(JsonFieldType.STRING).description("회원 이름"),
                            fieldWithPath("request.nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                            fieldWithPath("request.department").type(JsonFieldType.STRING).description("회원 소속 부서"),
                            fieldWithPath("request.position").type(JsonFieldType.STRING).description("회원 직위"),
                            fieldWithPath("request.phone").type(JsonFieldType.STRING).description("회원 전화번호"),
                            fieldWithPath("request.role").type(JsonFieldType.STRING).description("회원 역할"),
                            fieldWithPath("request.agitUrl").type(JsonFieldType.STRING).description("회원 아지트 URL"),
                            fieldWithPath("profileImage").type(MediaType.MULTIPART_FORM_DATA_VALUE).description("프로필 이미지")
                        ),
                        responseFields(
                            fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                            fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                            fieldWithPath("result.memberId").type(JsonFieldType.STRING).description("암호화된 회원 PK")
                        )
                    )
                );
        }

    }
}