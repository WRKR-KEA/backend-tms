package com.wrkr.tickety.domains.ticket.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.ticket.application.dto.request.GuideCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.GuideUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideDeleteUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideUpdateUseCase;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(controllers = GuideController.class)
class GuideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GuideGetUseCase guideGetUseCase;

    @MockitoBean
    private GuideCreateUseCase guideCreateUseCase;

    @MockitoBean
    private GuideDeleteUseCase guideDeleteUseCase;

    @MockitoBean
    private GuideUpdateUseCase guideUpdateUseCase;

    @BeforeAll
    static void beforeAll() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Test
    @DisplayName("도움말을 조회한다.")
    @WithMockCustomUser(username = "hjw", role = Role.USER, nickname = "user.hjw")
    void guideGetTest() throws Exception {
        //given
        String cryptoCategoryId = "LvI-mc4SwqpS3CixT1m_GQ";
        GuideResponse guideResponse = GuideResponse.builder()
                                                   .guideId(cryptoCategoryId)
                                                   .content("도움말 내용")
                                                   .attachmentUrls(null)
                                                   .build();
        given(guideGetUseCase.getGuide(3L)).willReturn(guideResponse);
        //when & then
        mockMvc.perform(get("/api/user/guide/{cryptoCategoryId}", cryptoCategoryId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result.guideId").value(cryptoCategoryId))
               .andExpect(jsonPath("$.result.content").value("도움말 내용"))
               .andExpect(jsonPath("$.result.attachmentUrls").isEmpty())
               .andDo(document("Guide/Get",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               resource(ResourceSnippetParameters.builder()
                                                                 .summary("도움말 조회 API")
                                                                 .description("주어진 암호화된 카테고리 키를 이용하여 도움말을 조회하는 API입니다.")
                                                                 .tag("Guide")
                                                                 .build()),
                               pathParameters(parameterWithName("cryptoCategoryId").description("암호화된 카테고리 키")),
                               responseFields(
                                   fieldWithPath("result").description("도움말 응답 객체 (GuideResponse)"),
                                   fieldWithPath("result.guideId").description("도움말 id"),
                                   fieldWithPath("result.content").description("도움말 내용"),
                                   fieldWithPath("result.attachmentUrls").description("첨부 파일 URL 목록"),
                                   fieldWithPath("isSuccess").description("성공 여부"),
                                   fieldWithPath("code").description("응답 코드"),
                                   fieldWithPath("message").description("응답 메시지")))
                     );
    }

    @Test
    @DisplayName("도움말을 생성한다.")
    @WithMockCustomUser(username = "hjw", role = Role.ADMIN, nickname = "admin.hjw")
    void guideCreateTest() throws Exception {
        //given
        String cryptoCategoryId = "LvI-mc4SwqpS3CixT1m_GQ";
        Long categoryId = 3L;
        GuideCreateRequest guideCreateRequest = GuideCreateRequest.builder()
                                                                  .content("도움말 내용")
                                                                  .build();
        PkResponse pkResponse = PkResponse.builder()
                                          .id(cryptoCategoryId)
                                          .build();

        List<MultipartFile> attachments = List.of(new MockMultipartFile(
            "attachments",
            "example.txt",
            "text/plain",
            "파일 내용".getBytes(StandardCharsets.UTF_8)
        ));

        MockMultipartFile mockAttachments = new MockMultipartFile(
            "attachments",
            "example.txt",
            "text/plain",
            "파일 내용".getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile jsonPart = new MockMultipartFile(
            "guideCreateRequest",
            "",
            "application/json",
            objectMapper.writeValueAsBytes(guideCreateRequest)
        );
        given(guideCreateUseCase.createGuide(any(GuideCreateRequest.class), anyLong(), anyList())).willReturn(pkResponse);
        //when & then
        mockMvc.perform(multipart("/api/admin/guide/{cryptoCategoryId}", cryptoCategoryId)
                            .file(jsonPart)
                            .file(mockAttachments)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .with(csrf())
                            .with(request -> {
                                request.setMethod("POST");
                                return request;
                            })
                       )
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result.id").value(cryptoCategoryId))
               .andDo(document("Guide/Create",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               resource(ResourceSnippetParameters.builder()
                                                                 .summary("도움말 생성 API")
                                                                 .description("주어진 암호화된 카테고리 키와 함께 요청 본문에 포함된 JSON 데이터(guideCreateRequest)와 첨부 파일(attachments)을 이용해 새로운 도움말을 생성하는 API입니다.")
                                                                 .tag("Guide")
                                                                 .build()),
                               pathParameters(parameterWithName("cryptoCategoryId").description("암호화된 카테고리 키")),
                               requestParts(partWithName("guideCreateRequest").description("도움말 요청 dto"),
                                            partWithName("attachments").description("첨부 파일 목록")),
                               responseFields(
                                   fieldWithPath("isSuccess").description("성공 여부"),
                                   fieldWithPath("result").description("도움말 응답 객체 (GuideResponse)"),
                                   fieldWithPath("result.id").description("생성된 도움말 id"),
                                   fieldWithPath("code").description("응답 코드"),
                                   fieldWithPath("message").description("응답 메시지")))
                     );
    }

    @Test
    @DisplayName("도움말을 수정한다.")
    @WithMockCustomUser(username = "hjw", role = Role.ADMIN, nickname = "admin.hjw")
    void updateGuideTest() throws Exception {
        //given
        String cryptoGuideId = "LvI-mc4SwqpS3CixT1m_GQ";
        GuideUpdateRequest guideUpdateRequest = GuideUpdateRequest.builder()
                                                                  .content("도움말 내용")
                                                                  .deleteAttachments(List.of("https://s3.example.com/file1.png"))
                                                                  .build();
        PkResponse pkResponse = PkResponse.builder()
                                          .id(cryptoGuideId)
                                          .build();

        MockMultipartFile mockNewAttachments = new MockMultipartFile(
            "newAttachments",
            "example.txt",
            "text/plain",
            "파일 내용".getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile jsonPart = new MockMultipartFile(
            "guideUpdateRequest",
            "",
            "application/json",
            objectMapper.writeValueAsBytes(guideUpdateRequest)
        );
        given(guideUpdateUseCase.modifyGuide(anyLong(), any(GuideUpdateRequest.class), anyList())).willReturn(pkResponse);
        //when & then
        mockMvc.perform(multipart("/api/admin/guide/{cryptoGuideId}", cryptoGuideId)
                            .file(jsonPart)
                            .file(mockNewAttachments)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .with(csrf())
                            .with(request -> {
                                request.setMethod("PATCH");
                                return request;
                            })
                       )
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result.id").value(cryptoGuideId))
               .andDo(document("Guide/Update",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               resource(ResourceSnippetParameters.builder()
                                                                 .summary("도움말 수정 API")
                                                                 .description("주어진 암호화된 카테고리 키와 함께 요청 본문에 포함된 JSON 데이터(guideCreateRequest)와 첨부 파일(attachments)을 이용해 새로운 도움말을 수정하는 API입니다.")
                                                                 .tag("Guide")
                                                                 .build()),
                               pathParameters(parameterWithName("cryptoGuideId").description("암호화된 도움말 키")),
                               requestParts(partWithName("guideUpdateRequest").description("도움말 수정 요청 데이터"),
                                            partWithName("newAttachments").description("새로 추가할 첨부파일 목록")),
                               responseFields(
                                   fieldWithPath("isSuccess").description("성공 여부"),
                                   fieldWithPath("message").description("응답 메시지"),
                                   fieldWithPath("result.id").description("수정된 도움말 id"),
                                   fieldWithPath("code").description("응답 코드")))
                     );
    }

    @Test
    @DisplayName("도움말을 삭제한다.")
    @WithMockCustomUser(username = "hjw", role = Role.ADMIN, nickname = "admin.hjw")
    void guideDeleteTest() throws Exception {
        //given
        String cryptoGuideId = "LvI-mc4SwqpS3CixT1m_GQ";
        given(guideDeleteUseCase.deleteGuide(3L)).willReturn(PkResponse.builder().id(cryptoGuideId).build());
        //when & then
        mockMvc.perform(delete("/api/admin/guide/{cryptoGuideId}", cryptoGuideId).with(csrf()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true))
               .andDo(document("Guide/Delete",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               resource(ResourceSnippetParameters.builder()
                                                                 .summary("도움말 삭제 API")
                                                                 .description("주어진 암호화된 도움말 키를 이용하여 도움말을 삭제하는 API입니다.")
                                                                 .tag("Guide")
                                                                 .build()),
                               pathParameters(parameterWithName("cryptoGuideId").description("암호화된 도움말 키")),
                               responseFields(
                                   fieldWithPath("isSuccess").description("성공 여부"),
                                   fieldWithPath("message").description("응답 메시지"),
                                   fieldWithPath("code").description("응답 코드"),
                                   fieldWithPath("result.id").description("삭제된 도움말 id")))
                     );
    }
}