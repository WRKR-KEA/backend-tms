package com.wrkr.tickety.domains.member.presentation;

import static com.wrkr.tickety.common.fixture.member.UserFixture.ADMIN_A;
import static com.wrkr.tickety.common.fixture.member.UserFixture.USER_J;
import static com.wrkr.tickety.domains.auth.exception.AuthErrorCode.PERMISSION_DENIED;
import static com.wrkr.tickety.domains.member.domain.constant.Role.ADMIN;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_EMAIL;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_NICKNAME;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_DEPARTMENT;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_EMAIL;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_NAME;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_NICKNAME;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_PHONE;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_POSITION;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_ROLE;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.wrkr.tickety.global.response.code.CommonErrorCode.BAD_REQUEST;
import static com.wrkr.tickety.global.response.code.SuccessCode.SUCCESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequestForExcel;
import com.wrkr.tickety.domains.member.application.dto.request.MemberDeleteRequest;
import com.wrkr.tickety.domains.member.application.dto.request.MemberInfoUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
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
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.excel.ExcelUtil;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
    @DisplayName("관리자 - 회원 등록 API 테스트")
    class CreateMemberTest {

        @Test
        @DisplayName("회원 등록에 성공한다.")
        @WithMockCustomUser(username = "admin", role = ADMIN, nickname = "admin.ad", memberId = 1L)
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
                "request.json",
                APPLICATION_JSON_VALUE,
                content.getBytes(StandardCharsets.UTF_8)
            );

            MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "profileImage.png",
                IMAGE_PNG_VALUE,
                "profileImage.png".getBytes()
            );

            when(memberCreateUseCase.createMember(any(MemberCreateRequest.class), any(MockMultipartFile.class))).thenReturn(
                MemberPkResponse.builder()
                    .memberId(encryptedMemberId)
                    .build()
            );

            // when
            MockHttpServletRequestBuilder requestBuilder = multipart("/api/admin/members")
                .file(requestJson)
                .file(profileImage)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .accept(APPLICATION_JSON)
                .with(csrf());

            // then
            mockMvc.perform(requestBuilder)
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.isSuccess").value(true),
                    jsonPath("$.code").value(SUCCESS.getCustomCode()),
                    jsonPath("$.message").value(SUCCESS.getMessage()),
                    jsonPath("$.result.memberId").value(encryptedMemberId)
                )
                .andDo(document(
                    "AdminMember/createMember/Request/Success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestParts(
                        partWithName("request").description("회원 정보 수정 요청 JSON 데이터"),
                        partWithName("profileImage").optional().description("프로필 사진 파일")
                    ),
                    requestPartFields("request",
                        fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일 주소"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                        fieldWithPath("department").type(JsonFieldType.STRING).description("회원 소속 부서"),
                        fieldWithPath("position").type(JsonFieldType.STRING).description("회원 직위"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("회원 전화번호"),
                        fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한"),
                        fieldWithPath("agitUrl").type(JsonFieldType.STRING).optional().description("회원 아지트 URL")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("result.memberId").type(JsonFieldType.STRING).description("암호화된 회원 PK")
                    )
                ));

        }

        @Test
        @DisplayName("회원 등록 시 잘못된 입력 값으로 인해 BAD_REQUEST 예외가 발생한다.")
        @WithMockCustomUser(username = "admin", role = ADMIN, nickname = "admin.ad", memberId = 1L)
        void createMemberValidationException() throws Exception {
            // given
            MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
                .email(null)
                .name(null)
                .nickname(null)
                .department(null)
                .position(null)
                .phone(null)
                .role(null)
                .agitUrl(null)
                .build();

            String content = objectMapper.writeValueAsString(memberCreateRequest);
            MockMultipartFile requestJson = new MockMultipartFile(
                "request",
                "request.json",
                APPLICATION_JSON_VALUE,
                content.getBytes(StandardCharsets.UTF_8)
            );

            MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "profileImage.png",
                IMAGE_PNG_VALUE,
                "profileImage.png".getBytes()
            );

            // when
            MockHttpServletRequestBuilder requestBuilder = multipart("/api/admin/members")
                .file(requestJson)
                .file(profileImage)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .accept(APPLICATION_JSON)
                .with(csrf());

            // then
            mockMvc.perform(requestBuilder)
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").value(BAD_REQUEST.getCustomCode()),
                    jsonPath("$.message").value(BAD_REQUEST.getMessage()),
                    jsonPath("$.result.name").value(INVALID_NAME.getMessage()),
                    jsonPath("$.result.department").value(INVALID_DEPARTMENT.getMessage()),
                    jsonPath("$.result.role").value(INVALID_ROLE.getMessage()),
                    jsonPath("$.result.position").value(INVALID_POSITION.getMessage()),
                    jsonPath("$.result.phone").value(INVALID_PHONE.getMessage()),
                    jsonPath("$.result.email").value(INVALID_EMAIL.getMessage() + " (허용된 이메일: gachon.ac.kr, gmail.com)"),
                    jsonPath("$.result.nickname").value(INVALID_NICKNAME.getMessage())
                )
                .andDo(document(
                    "AdminMember/createMember/Request/Failure/BadRequest",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestParts(
                        partWithName("request").description("회원 등록 요청 JSON 데이터"),
                        partWithName("profileImage").optional().description("프로필 사진 파일")
                    ),
                    requestPartFields("request",
                        fieldWithPath("email").type(JsonFieldType.STRING).optional().description("회원 이메일 주소 (필수 입력)"),
                        fieldWithPath("name").type(JsonFieldType.STRING).optional().description("회원 이름 (필수 입력)"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).optional().description("회원 닉네임 (필수 입력)"),
                        fieldWithPath("department").type(JsonFieldType.STRING).optional().description("회원 소속 부서 (필수 입력)"),
                        fieldWithPath("position").type(JsonFieldType.STRING).optional().description("회원 직위 (필수 입력)"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).optional().description("회원 전화번호 (필수 입력)"),
                        fieldWithPath("role").type(JsonFieldType.STRING).optional().description("회원 권한 (유효한 값 필요, USER| ADMIN)"),
                        fieldWithPath("agitUrl").type(JsonFieldType.STRING).optional().description("회원 아지트 URL (선택 사항)")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("result.name").type(JsonFieldType.STRING).description("이름 관련 유효성 검사 오류 메시지"),
                        fieldWithPath("result.department").type(JsonFieldType.STRING).description("부서 관련 유효성 검사 오류 메시지"),
                        fieldWithPath("result.role").type(JsonFieldType.STRING).description("역할 관련 유효성 검사 오류 메시지"),
                        fieldWithPath("result.position").type(JsonFieldType.STRING).description("직책 관련 유효성 검사 오류 메시지"),
                        fieldWithPath("result.phone").type(JsonFieldType.STRING).description("전화번호 관련 유효성 검사 오류 메시지"),
                        fieldWithPath("result.email").type(JsonFieldType.STRING).description("이메일 관련 유효성 검사 오류 메시지"),
                        fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("아이디 관련 유효성 검사 오류 메시지")
                    )
                ));
        }


        @Test
        @DisplayName("이미 사용 중인 이메일로 회원 등록 시 ALREADY_EXIST_EMAIL 예외가 발생한다.")
        @WithMockCustomUser(username = "admin", role = ADMIN, nickname = "admin.ad", memberId = 1L)
        void createMemberWithDuplicateEmail() throws Exception {
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

            String content = objectMapper.writeValueAsString(memberCreateRequest);
            MockMultipartFile requestJson = new MockMultipartFile(
                "request",
                "request.json",
                APPLICATION_JSON_VALUE,
                content.getBytes(StandardCharsets.UTF_8)
            );

            MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "profileImage.png",
                IMAGE_PNG_VALUE,
                "profileImage.png".getBytes()
            );

            doThrow(new ApplicationException(ALREADY_EXIST_EMAIL)).when(memberCreateUseCase)
                .createMember(any(MemberCreateRequest.class), any(MockMultipartFile.class));

            // when
            MockHttpServletRequestBuilder requestBuilder = multipart("/api/admin/members")
                .file(requestJson)
                .file(profileImage)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .accept(APPLICATION_JSON)
                .with(csrf());

            // then
            mockMvc.perform(requestBuilder)
                .andExpectAll(
                    status().isConflict(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").value(ALREADY_EXIST_EMAIL.getCustomCode()),
                    jsonPath("$.message").value(ALREADY_EXIST_EMAIL.getMessage())
                )
                .andDo(document(
                    "AdminMember/createMember/Request/Failure/DuplicateEmail",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestParts(
                        partWithName("request").description("회원 정보 수정 요청 JSON 데이터"),
                        partWithName("profileImage").optional().description("프로필 사진 파일")
                    ),
                    requestPartFields("request",
                        fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일 주소"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                        fieldWithPath("department").type(JsonFieldType.STRING).description("회원 소속 부서"),
                        fieldWithPath("position").type(JsonFieldType.STRING).description("회원 직위"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("회원 전화번호"),
                        fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한"),
                        fieldWithPath("agitUrl").type(JsonFieldType.STRING).optional().description("회원 아지트 URL")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                    )
                ));
        }


        @Test
        @DisplayName("이미 사용 중인 아이디(닉네임)로 회원 등록 시 ALREADY_EXIST_NICKNAME 예외가 발생한다.")
        @WithMockUser(username = "admin", roles = "ADMIN")
        void createMemberWithDuplicateNickname() throws Exception {
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

            String content = objectMapper.writeValueAsString(memberCreateRequest);
            MockMultipartFile requestJson = new MockMultipartFile(
                "request",
                "request.json",
                APPLICATION_JSON_VALUE,
                content.getBytes(StandardCharsets.UTF_8)
            );

            MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "profileImage.png",
                IMAGE_PNG_VALUE,
                "profileImage".getBytes()
            );

            doThrow(new ApplicationException(ALREADY_EXIST_NICKNAME)).when(memberCreateUseCase)
                .createMember(any(MemberCreateRequest.class), any(MockMultipartFile.class));

            // when
            ResultActions requestBuilder = mockMvc.perform(multipart("/api/admin/members")
                .file(requestJson)
                .file(profileImage)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .accept(APPLICATION_JSON)
                .with(csrf())
            );

            // then
            requestBuilder
                .andExpectAll(
                    status().isConflict(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").value(ALREADY_EXIST_NICKNAME.getCustomCode()),
                    jsonPath("$.message").value(ALREADY_EXIST_NICKNAME.getMessage())
                )
                .andDo(document(
                    "AdminMember/createMember/Request/Failure/DuplicateNickname",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestParts(
                        partWithName("request").description("회원 정보 수정 요청 JSON 데이터"),
                        partWithName("profileImage").optional().description("프로필 사진 파일")
                    ),
                    requestPartFields("request",
                        fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일 주소"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                        fieldWithPath("department").type(JsonFieldType.STRING).description("회원 소속 부서"),
                        fieldWithPath("position").type(JsonFieldType.STRING).description("회원 직위"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("회원 전화번호"),
                        fieldWithPath("role").type(JsonFieldType.STRING).description("회원 권한"),
                        fieldWithPath("agitUrl").type(JsonFieldType.STRING).optional().description("회원 아지트 URL")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                    )
                ));
        }


    }

    @Nested
    @DisplayName("관리자 - 회원 정보 수정 API 테스트")
    class ModifyMemberInfoTest {

        @Test
        @DisplayName("회원 정보를 성공적으로 수정한다.")
        @WithMockCustomUser(username = "admin", role = ADMIN, nickname = "admin.ad", memberId = 1L)
        void modifyMemberInfoSuccess() throws Exception {
            // given
            Member userMember = USER_J.toMember();
            String encryptedMemberId = pkCrypto.encryptValue(userMember.getMemberId());

            MemberInfoUpdateRequest updateRequest = MemberInfoUpdateRequest.builder()
                .email(userMember.getEmail())
                .name(userMember.getName())
                .nickname(userMember.getNickname())
                .department(userMember.getDepartment())
                .position(userMember.getPosition())
                .phone(userMember.getPhone())
                .agitUrl("http://new-agit-url.com")
                .build();

            String requestJson = objectMapper.writeValueAsString(updateRequest);
            MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "request.json",
                APPLICATION_JSON_VALUE,
                requestJson.getBytes(StandardCharsets.UTF_8)
            );

            MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "profileImage.png",
                IMAGE_PNG_VALUE,
                "profileImage".getBytes()
            );

            MemberPkResponse response = MemberPkResponse.builder()
                .memberId(encryptedMemberId)
                .build();

            when(memberInfoUpdateUseCase.modifyMemberInfo(any(), any(), any())).thenReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(PATCH, "/api/admin/members/{memberId}", encryptedMemberId)
                .file(requestPart)
                .file(profileImage)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .accept(APPLICATION_JSON)
                .with(csrf());

            // then
            mockMvc.perform(requestBuilder)
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.isSuccess").value(true),
                    jsonPath("$.code").value(SUCCESS.getCustomCode()),
                    jsonPath("$.message").value(SUCCESS.getMessage()),
                    jsonPath("$.result.memberId").value(encryptedMemberId)
                )
                .andDo(document(
                    "AdminMember/modifyMemberInfo/Request/Success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestParts(
                        partWithName("request").description("회원 정보 수정 요청 JSON 데이터"),
                        partWithName("profileImage").optional().description("프로필 사진 파일")
                    ),
                    requestPartFields("request",
                        fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일 주소"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                        fieldWithPath("department").type(JsonFieldType.STRING).description("회원 소속 부서"),
                        fieldWithPath("position").type(JsonFieldType.STRING).description("회원 직위"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("회원 전화번호"),
                        fieldWithPath("agitUrl").type(JsonFieldType.STRING).optional().description("회원 아지트 URL")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("result.memberId").type(JsonFieldType.STRING).description("암호화된 회원 PK")
                    )
                ));
        }

        @Test
        @DisplayName("회원 정보 수정시 잘못된 입력 값으로 인해 BAD_REQUEST 예외가 발생한다.")
        @WithMockCustomUser(username = "admin", role = ADMIN, nickname = "admin.ad", memberId = 1L)
        void createMemberValidationException() throws Exception {
            // given
            Member userMember = USER_J.toMember();
            String encryptedMemberId = pkCrypto.encryptValue(userMember.getMemberId());

            MemberInfoUpdateRequest memberInfoUpdateRequest = MemberInfoUpdateRequest.builder()
                .email(null)
                .name(null)
                .nickname(null)
                .department(null)
                .position(null)
                .phone(null)
                .agitUrl(null)
                .build();

            String content = objectMapper.writeValueAsString(memberInfoUpdateRequest);
            MockMultipartFile requestJson = new MockMultipartFile(
                "request",
                "request.json",
                APPLICATION_JSON_VALUE,
                content.getBytes(StandardCharsets.UTF_8)
            );

            MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "profileImage.png",
                IMAGE_PNG_VALUE,
                "profileImage.png".getBytes()
            );

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(PATCH, "/api/admin/members/{memberId}", encryptedMemberId)
                .file(requestJson)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .accept(APPLICATION_JSON)
                .with(csrf());

            // then
            mockMvc.perform(requestBuilder)
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").value(BAD_REQUEST.getCustomCode()),
                    jsonPath("$.message").value(BAD_REQUEST.getMessage()),
                    jsonPath("$.result.name").value(INVALID_NAME.getMessage()),
                    jsonPath("$.result.department").value(INVALID_DEPARTMENT.getMessage()),
                    jsonPath("$.result.position").value(INVALID_POSITION.getMessage()),
                    jsonPath("$.result.phone").value(INVALID_PHONE.getMessage()),
                    jsonPath("$.result.email").value(INVALID_EMAIL.getMessage() + " (허용된 이메일: gachon.ac.kr, gmail.com)"),
                    jsonPath("$.result.nickname").value(INVALID_NICKNAME.getMessage())
                )
                .andDo(document(
                    "AdminMember/modifyMemberInfo/Request/Failure/BadRequest",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestParts(
                        partWithName("request").description("회원 등록 요청 JSON 데이터"),
                        partWithName("profileImage").optional().description("프로필 사진 파일")
                    ),
                    requestPartFields("request",
                        fieldWithPath("email").type(JsonFieldType.STRING).optional().description("회원 이메일 주소 (필수 입력)"),
                        fieldWithPath("name").type(JsonFieldType.STRING).optional().description("회원 이름 (필수 입력)"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).optional().description("회원 닉네임 (필수 입력)"),
                        fieldWithPath("department").type(JsonFieldType.STRING).optional().description("회원 소속 부서 (필수 입력)"),
                        fieldWithPath("position").type(JsonFieldType.STRING).optional().description("회원 직위 (필수 입력)"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).optional().description("회원 전화번호 (필수 입력)"),
                        fieldWithPath("agitUrl").type(JsonFieldType.STRING).optional().description("회원 아지트 URL (선택 사항)")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("result.name").type(JsonFieldType.STRING).description("이름 관련 유효성 검사 오류 메시지"),
                        fieldWithPath("result.department").type(JsonFieldType.STRING).description("부서 관련 유효성 검사 오류 메시지"),
                        fieldWithPath("result.position").type(JsonFieldType.STRING).description("직책 관련 유효성 검사 오류 메시지"),
                        fieldWithPath("result.phone").type(JsonFieldType.STRING).description("전화번호 관련 유효성 검사 오류 메시지"),
                        fieldWithPath("result.email").type(JsonFieldType.STRING).description("이메일 관련 유효성 검사 오류 메시지"),
                        fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("아이디 관련 유효성 검사 오류 메시지")
                    )
                ));
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID로 수정 요청 시 MEMBER_NOT_FOUND 예외를 발생시킨다.")
        @WithMockCustomUser(username = "admin", role = ADMIN, nickname = "admin.ad", memberId = 1L)
        void modifyMemberInfoNotFound() throws Exception {
            // given
            String nonExistentMemberId = "999";
            Member userMember = USER_J.toMember();

            MemberInfoUpdateRequest updateRequest = MemberInfoUpdateRequest.builder()
                .email(userMember.getEmail())
                .name(userMember.getName())
                .nickname(userMember.getNickname())
                .department(userMember.getDepartment())
                .position(userMember.getPosition())
                .phone(userMember.getPhone())
                .agitUrl("http://new-agit-url.com")
                .build();

            String requestJson = objectMapper.writeValueAsString(updateRequest);
            MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "request.json",
                APPLICATION_JSON_VALUE,
                requestJson.getBytes(StandardCharsets.UTF_8)
            );

            doThrow(ApplicationException.from(MEMBER_NOT_FOUND)).when(memberInfoUpdateUseCase)
                .modifyMemberInfo(any(), any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(PATCH, "/api/admin/members/{memberId}", nonExistentMemberId)
                .file(requestPart)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .accept(APPLICATION_JSON)
                .with(csrf());

            // then
            mockMvc.perform(requestBuilder)
                .andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").value(MEMBER_NOT_FOUND.getCustomCode()),
                    jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage())
                )
                .andDo(document(
                    "AdminMember/modifyMemberInfo/Request/Failure/MemberNotFound",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestParts(
                        partWithName("request").description("회원 정보 수정 요청 JSON 데이터"),
                        partWithName("profileImage").optional().description("프로필 사진 파일")
                    ),
                    requestPartFields("request",
                        fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일 주소"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                        fieldWithPath("department").type(JsonFieldType.STRING).description("회원 소속 부서"),
                        fieldWithPath("position").type(JsonFieldType.STRING).description("회원 직위"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("회원 전화번호"),
                        fieldWithPath("agitUrl").type(JsonFieldType.STRING).optional().description("회원 아지트 URL")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                    )
                ));
        }

        @Test
        @DisplayName("이메일이 이미 사용 중일 경우 ALREADY_EXIST_EMAIL 예외를 발생시킨다.")
        @WithMockCustomUser(username = "admin", role = ADMIN, nickname = "admin.ad", memberId = 1L)
        void modifyMemberInfoWithDuplicateEmailException() throws Exception {
            // given
            Member userMember = USER_J.toMember(); // USER_J에서 회원 정보를 가져옴
            String encryptedMemberId = pkCrypto.encryptValue(userMember.getMemberId());

            MemberInfoUpdateRequest updateRequest = MemberInfoUpdateRequest.builder()
                .email(userMember.getEmail())
                .name(userMember.getName())
                .nickname(userMember.getNickname())
                .department(userMember.getDepartment())
                .position(userMember.getPosition())
                .phone(userMember.getPhone())
                .agitUrl("http://new-agit-url.com")
                .build();

            String requestJson = objectMapper.writeValueAsString(updateRequest);
            MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "request.json",
                APPLICATION_JSON_VALUE,
                requestJson.getBytes(StandardCharsets.UTF_8)
            );

            doThrow(ApplicationException.from(ALREADY_EXIST_EMAIL)).when(memberInfoUpdateUseCase)
                .modifyMemberInfo(any(), any(), any());

            // when & then
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(PATCH, "/api/admin/members/{memberId}", encryptedMemberId)
                .file(requestPart)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .accept(APPLICATION_JSON)
                .with(csrf());

            mockMvc.perform(requestBuilder)
                .andExpectAll(
                    status().isConflict(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").value(ALREADY_EXIST_EMAIL.getCustomCode()),
                    jsonPath("$.message").value(ALREADY_EXIST_EMAIL.getMessage())
                )
                .andDo(document(
                    "AdminMember/modifyMemberInfo/Request/Failure/EmailDuplicate",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestParts(
                        partWithName("request").description("회원 정보 수정 요청 JSON 데이터"),
                        partWithName("profileImage").optional().description("프로필 사진 파일")
                    ),
                    requestPartFields("request",
                        fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일 주소"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                        fieldWithPath("department").type(JsonFieldType.STRING).description("회원 소속 부서"),
                        fieldWithPath("position").type(JsonFieldType.STRING).description("회원 직위"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("회원 전화번호"),
                        fieldWithPath("agitUrl").type(JsonFieldType.STRING).optional().description("회원 아지트 URL")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                    )
                ));
        }

        @Test
        @DisplayName("닉네임이 이미 사용 중일 경우 ALREADY_EXIST_NICKNAME 예외를 발생시킨다.")
        @WithMockCustomUser(username = "admin", role = ADMIN, nickname = "admin.ad", memberId = 1L)
        void modifyMemberInfoWithDuplicateNicknameException() throws Exception {
            // given
            Member userMember = USER_J.toMember(); // USER_J에서 회원 정보를 가져옴
            String encryptedMemberId = pkCrypto.encryptValue(userMember.getMemberId());

            MemberInfoUpdateRequest updateRequest = MemberInfoUpdateRequest.builder()
                .email(userMember.getEmail())
                .name(userMember.getName())
                .nickname(userMember.getNickname())
                .department(userMember.getDepartment())
                .position(userMember.getPosition())
                .phone(userMember.getPhone())
                .agitUrl("http://new-agit-url.com")
                .build();

            String requestJson = objectMapper.writeValueAsString(updateRequest);
            MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "request.json",
                APPLICATION_JSON_VALUE,
                requestJson.getBytes(StandardCharsets.UTF_8)
            );

            doThrow(ApplicationException.from(ALREADY_EXIST_NICKNAME)).when(memberInfoUpdateUseCase)
                .modifyMemberInfo(any(), any(), any());

            // when & then
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(PATCH, "/api/admin/members/{memberId}", encryptedMemberId)
                .file(requestPart)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .accept(APPLICATION_JSON)
                .with(csrf());

            mockMvc.perform(requestBuilder)
                .andExpectAll(
                    status().isConflict(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").value(ALREADY_EXIST_NICKNAME.getCustomCode()),
                    jsonPath("$.message").value(ALREADY_EXIST_NICKNAME.getMessage())
                )
                .andDo(document(
                    "AdminMember/modifyMemberInfo/Request/Failure/DuplicateNickname",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestParts(
                        partWithName("request").description("회원 정보 수정 요청 JSON 데이터"),
                        partWithName("profileImage").optional().description("프로필 사진 파일")
                    ),
                    requestPartFields("request",
                        fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일 주소"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                        fieldWithPath("department").type(JsonFieldType.STRING).description("회원 소속 부서"),
                        fieldWithPath("position").type(JsonFieldType.STRING).description("회원 직위"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("회원 전화번호"),
                        fieldWithPath("agitUrl").type(JsonFieldType.STRING).optional().description("회원 아지트 URL")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                    )
                ));
        }


        @Test
        @DisplayName("권한이 ADMIN인 회원의 정보를 수정하려고 할 때 PERMISSION_DENIED 예외가 발생한다.")
        @WithMockCustomUser(username = "admin", role = ADMIN, nickname = "admin.ad", memberId = 1L)
        void modifyMemberInfoWithAdminAuthorizationException() throws Exception {
            // given
            Member adminMember = ADMIN_A.toMember();
            String encryptedMemberId = pkCrypto.encryptValue(adminMember.getMemberId());

            MemberInfoUpdateRequest updateRequest = MemberInfoUpdateRequest.builder()
                .email(adminMember.getEmail())
                .name(adminMember.getName())
                .nickname(adminMember.getNickname())
                .department(adminMember.getDepartment())
                .position(adminMember.getPosition())
                .phone(adminMember.getPhone())
                .agitUrl("http://new-agit-url.com")
                .build();

            String requestJson = objectMapper.writeValueAsString(updateRequest);
            MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "request.json",
                APPLICATION_JSON_VALUE,
                requestJson.getBytes(StandardCharsets.UTF_8)
            );

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(PATCH, "/api/admin/members/{memberId}", encryptedMemberId)
                .file(requestPart)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .accept(APPLICATION_JSON)
                .with(csrf());

            doThrow(ApplicationException.from(PERMISSION_DENIED)).when(memberInfoUpdateUseCase)
                .modifyMemberInfo(any(), any(), any());

            // then
            mockMvc.perform(requestBuilder)
                .andExpectAll(
                    status().isForbidden(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").value(PERMISSION_DENIED.getCustomCode()),
                    jsonPath("$.message").value(PERMISSION_DENIED.getMessage())
                )
                .andDo(document(
                    "AdminMember/modifyMemberInfo/Request/Failure/PermissionDenied",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestParts(
                        partWithName("request").description("회원 정보 수정 요청 JSON 데이터"),
                        partWithName("profileImage").optional().description("프로필 사진 파일")
                    ),
                    requestPartFields("request",
                        fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일 주소"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                        fieldWithPath("department").type(JsonFieldType.STRING).description("회원 소속 부서"),
                        fieldWithPath("position").type(JsonFieldType.STRING).description("회원 직위"),
                        fieldWithPath("phone").type(JsonFieldType.STRING).description("회원 전화번호"),
                        fieldWithPath("agitUrl").type(JsonFieldType.STRING).optional().description("회원 아지트 URL")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                    )
                ));
        }

    }


    @Nested
    @DisplayName("관리자 - 선택한 회원 삭제 API 테스트")
    class SoftDeleteMemberTest {

        @Test
        @DisplayName("회원 삭제가 성공적으로 수행된다.")
        @WithMockCustomUser(username = "admin", role = ADMIN, nickname = "admin.ad", memberId = 1L)
        void softDeleteMemberSuccess() throws Exception {
            // given
            List<String> memberIdList = List.of("encryptedId1", "encryptedId2");
            MemberDeleteRequest deleteRequest = MemberDeleteRequest.builder()
                .memberIdList(memberIdList)
                .build();

            // when
            ResultActions requestBuilder = mockMvc.perform(RestDocumentationRequestBuilders
                .delete("/api/admin/members")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest))
                .with(csrf()));

            // then
            requestBuilder
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.isSuccess").value(true),
                    jsonPath("$.code").value(SUCCESS.getCustomCode()),
                    jsonPath("$.message").value(SUCCESS.getMessage())
                )
                .andDo(document(
                    "AdminMember/softDeleteMember/Request/Success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("memberIdList").type(JsonFieldType.ARRAY).description("암호화된 회원 PK 리스트")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                    )
                ));
            verify(memberInfoUpdateUseCase).softDeleteMember(memberIdList);

        }

        @Test
        @DisplayName("존재하지 않는 회원을 삭제하려고 할 경우 MEMBER_NOT_FOUND 예외를 발생시킨다.")
        @WithMockCustomUser(username = "admin", role = ADMIN, nickname = "admin.ad", memberId = 1L)
        void softDeleteMemberNotFound() throws Exception {
            // given
            List<String> memberIdList = List.of("nonExistentId");
            MemberDeleteRequest deleteRequest = new MemberDeleteRequest(memberIdList);

            doThrow(ApplicationException.from(MEMBER_NOT_FOUND)).when(memberInfoUpdateUseCase)
                .softDeleteMember(any());

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders
                    .delete("/api/admin/members")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(deleteRequest))
                    .with(csrf()))
                .andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").value(MEMBER_NOT_FOUND.getCustomCode()),
                    jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage())
                )
                .andDo(document(
                    "AdminMember/softDeleteMember/Request/Failure/MemberNotFound",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("memberIdList").type(JsonFieldType.ARRAY).description("암호화된 회원 PK 리스트")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                    )
                ));

        }

        @Test
        @DisplayName("삭제하려는 회원이 ADMIN인 경우 PERMISSION_DENIED 예외를 발생시킨다.")
        @WithMockCustomUser(username = "admin", role = ADMIN, nickname = "admin.ad", memberId = 1L)
        void softDeleteMemberWithAdminAuthorizationException() throws Exception {
            // given
            Member adminMember = ADMIN_A.toMember();
            String encryptedAdminId = pkCrypto.encryptValue(adminMember.getMemberId());
            List<String> memberIdList = List.of(encryptedAdminId);
            MemberDeleteRequest deleteRequest = MemberDeleteRequest.builder()
                .memberIdList(memberIdList)
                .build();

            doThrow(ApplicationException.from(PERMISSION_DENIED)).when(memberInfoUpdateUseCase)
                .softDeleteMember(any());

            // when
            ResultActions requestBuilder = mockMvc.perform(RestDocumentationRequestBuilders
                .delete("/api/admin/members")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest))
                .with(csrf()));

            // then
            requestBuilder
                .andExpectAll(
                    status().isForbidden(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").value(PERMISSION_DENIED.getCustomCode()),
                    jsonPath("$.message").value(PERMISSION_DENIED.getMessage())
                )
                .andDo(document(
                    "AdminMember/softDeleteMember/Request/Failure/PermissionDenied",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("memberIdList").type(JsonFieldType.ARRAY).description("암호화된 회원 PK 리스트")
                    ),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                    )
                ));
        }
    }

    @Nested
    @DisplayName("관리자 - 회원 정보 조회 API 테스트")
    class GetMemberInfoTest {

        @Test
        @DisplayName("회원 정보를 성공적으로 조회한다.")
        @WithMockCustomUser(username = "admin", role = ADMIN, nickname = "admin.ad", memberId = 1L)
        void getMemberInfoSuccess() throws Exception {
            // given
            Member member = USER_J.toMember();
            String encryptedMemberId = pkCrypto.encryptValue(member.getMemberId());

            MemberInfoResponse memberInfoResponse = MemberMapper.mapToMemberInfoResponse(member);

            when(memberInfoGetUseCase.getMemberInfo(encryptedMemberId)).thenReturn(memberInfoResponse);

            // when
            ResultActions requestBuilder = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/api/admin/members/{memberId}", encryptedMemberId)
                .accept(APPLICATION_JSON)
                .with(csrf()));

            // then
            requestBuilder
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.isSuccess").value(true),
                    jsonPath("$.code").value(SUCCESS.getCustomCode()),
                    jsonPath("$.message").value(SUCCESS.getMessage()),
                    jsonPath("$.result.email").value(memberInfoResponse.email()),
                    jsonPath("$.result.name").value(memberInfoResponse.name()),
                    jsonPath("$.result.nickname").value(memberInfoResponse.nickname()),
                    jsonPath("$.result.department").value(memberInfoResponse.department()),
                    jsonPath("$.result.position").value(memberInfoResponse.position()),
                    jsonPath("$.result.phone").value(memberInfoResponse.phone()),
                    jsonPath("$.result.role").value(memberInfoResponse.role()),
                    jsonPath("$.result.agitUrl").value(memberInfoResponse.agitUrl())
                )
                .andDo(document(
                    "AdminMember/getMemberInfo/Request/Success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("result.memberId").type(JsonFieldType.STRING).description("암호화된 회원 PK"),
                        fieldWithPath("result.profileImage").type(JsonFieldType.STRING).description("회원 프로필 이미지 URL"),
                        fieldWithPath("result.email").type(JsonFieldType.STRING).description("회원 이메일 주소"),
                        fieldWithPath("result.name").type(JsonFieldType.STRING).description("회원 이름"),
                        fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                        fieldWithPath("result.department").type(JsonFieldType.STRING).description("회원 소속 부서"),
                        fieldWithPath("result.position").type(JsonFieldType.STRING).description("회원 직위"),
                        fieldWithPath("result.phone").type(JsonFieldType.STRING).description("회원 전화번호"),
                        fieldWithPath("result.role").type(JsonFieldType.STRING).description("회원 역할"),
                        fieldWithPath("result.agitUrl").type(JsonFieldType.STRING).optional().description("회원 아지트 URL")
                    )
                ));
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID로 요청할 경우 MEMBER_NOT_FOUND 예외를 발생시킨다.")
        @WithMockCustomUser(username = "admin", role = ADMIN, nickname = "admin.ad", memberId = 1L)
        void getMemberInfoNotFound() throws Exception {
            // given
            String memberId = "999";
            when(memberInfoGetUseCase.getMemberInfo(memberId)).thenThrow(ApplicationException.from(MEMBER_NOT_FOUND));

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders
                    .get("/api/admin/members/{memberId}", memberId)
                    .accept(APPLICATION_JSON)
                    .with(csrf()))
                .andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.code").value(MEMBER_NOT_FOUND.getCustomCode()),
                    jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage())
                )
                .andDo(document(
                    "AdminMember/getMemberInfo/Request/Failure/MemberNotFound",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("응답 성공 여부"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                    )
                ));
        }
    }

    @Nested
    @DisplayName("관리자 - 회원 등록 엑셀 양식 다운로드 API 테스트")
    class DownloadMemberExcelTemplateTest {

        @Test
        @DisplayName("엑셀 양식 다운로드를 성공적으로 수행한다.")
        @WithMockCustomUser(username = "admin", role = Role.ADMIN, nickname = "admin.ad", memberId = 1L)
        void downloadMemberExcelTemplate_Success() throws Exception {
            // Given
            List<MemberCreateRequestForExcel> dummyData = List.of(
                new MemberCreateRequestForExcel(
                    "wrkr.kea@gachon.ac.kr", "김가천", "gachon.kim",
                    "인프라 지원팀", "네트워크 엔지니어", "010-1234-5678",
                    Role.MANAGER, "https://i.ibb.co/7Fd4Hhx/tickety-default-image.jpg",
                    "https://example.com/agit"
                )
            );

            doReturn(dummyData).when(excelExampleCreateUseCase).createMemberInfoExample();

            // When & Then
            mockMvc.perform(RestDocumentationRequestBuilders
                    .get("/api/admin/members/excel/example")
                    .with(csrf()))
                .andExpect(status().isOk())
                .andDo(document(
                    "AdminMemberApi/DownloadExcelTemplate/Success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint())
                ));
        }

        @Test
        @DisplayName("엑셀 양식 다운로드 중 내부 서버 오류 발생 시 실패한다.")
        @WithMockCustomUser(username = "admin", role = Role.ADMIN, nickname = "admin.ad", memberId = 1L)
        void downloadMemberExcelTemplate_Fail_InternalServerError() throws Exception {
            // Given
            doThrow(ApplicationException.from(CommonErrorCode.INTERNAL_SERVER_ERROR))
                .when(excelExampleCreateUseCase).createMemberInfoExample();

            // When & Then
            mockMvc.perform(RestDocumentationRequestBuilders
                    .get("/api/admin/members/excel/example")
                    .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(document(
                    "AdminMemberApi/DownloadExcelTemplate/Failure/InternalServerError",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint())
                ));
        }

        @Test
        @DisplayName("인증 정보가 없을 경우 엑셀 양식 다운로드가 실패한다.")
        void downloadMemberExcelTemplate_Fail_Unauthorized() throws Exception {
            // When & Then
            mockMvc.perform(RestDocumentationRequestBuilders
                    .get("/api/admin/members/excel/example")
                    .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(document(
                    "AdminMemberApi/DownloadExcelTemplate/Failure/Unauthorized",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint())
                ));
        }
    }


}