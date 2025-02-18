package com.wrkr.tickety.domains.ticket.presentation;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.ticket.application.dto.request.template.AdminTemplateCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.template.AdminTemplateUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplateGetResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplatePKResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.template.TemplateCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.template.TemplateDeleteUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.template.TemplateGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.template.TemplateUpdateUseCase;
import com.wrkr.tickety.global.annotation.WithMockCustomUser;
import com.wrkr.tickety.global.config.SecurityTestConfig;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TemplateController.class)
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
class TemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private TemplateGetUseCase templateGetUseCase;

    @MockitoBean
    private TemplateCreateUseCase templateCreateUseCase;

    @MockitoBean
    private TemplateUpdateUseCase templateUpdateUseCase;

    @MockitoBean
    private TemplateDeleteUseCase templateDeleteUseCase;

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    private static final Long CATEGORY_ID = 1L;
    private static final Long TEMPLATE_ID = 1L;

    @Nested
    @DisplayName("사용자, 관리자 템플릿 조회 API [GET /api/user/templates/{categoryId}]")
    class GetTemplate {

        @Test
        @WithMockCustomUser(username = "user", role = Role.USER)
        @DisplayName("템플릿 조회에 성공한다.")
        void getTemplate_Success() throws Exception {
            // given
            TemplateGetResponse response = TemplateGetResponse.builder()
                .templateId(PkCrypto.encrypt(TEMPLATE_ID))
                .categoryId(PkCrypto.encrypt(CATEGORY_ID))
                .content("템플릿 내용")
                .build();
            given(templateGetUseCase.getTemplate(anyLong())).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/user/templates/{categoryId}", PkCrypto.encrypt(CATEGORY_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.templateId").value(PkCrypto.encrypt(TEMPLATE_ID)))
                .andExpect(jsonPath("$.result.categoryId").value(PkCrypto.encrypt(CATEGORY_ID)))
                .andExpect(jsonPath("$.result.content").value("템플릿 내용"))
                .andDo(
                    document(
                        "UserTemplateApi/getTemplate",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                            parameterWithName("categoryId").description("조회할 템플릿의 카테고리 ID")
                        ),
                        responseFields(
                            fieldWithPath("isSuccess").description("성공 여부"),
                            fieldWithPath("code").description("응답 코드"),
                            fieldWithPath("message").description("응답 메시지"),
                            fieldWithPath("result").description("반환 결과"),
                            fieldWithPath("result.templateId").description("템플릿 ID"),
                            fieldWithPath("result.categoryId").description("카테고리 ID"),
                            fieldWithPath("result.content").description("템플릿 내용")
                        )
                    ))
            ;

        }
    }

    @Nested
    @WithMockCustomUser(username = "admin", role = Role.ADMIN)
    @DisplayName("관리자 템플릿 추가 API [POST /api/admin/templates]")
    class CreateTemplate {

        @Test
        @DisplayName("템플릿 추가에 성공한다.")
        void createTemplate_Success() throws Exception {
            // given
            AdminTemplateCreateRequest request = new AdminTemplateCreateRequest(PkCrypto.encrypt(CATEGORY_ID), "템플릿 내용");
            TemplatePKResponse response = new TemplatePKResponse(PkCrypto.encrypt(TEMPLATE_ID));
            given(templateCreateUseCase.createTemplate(request)).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/admin/templates")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.templateId").value(PkCrypto.encrypt(TEMPLATE_ID)))
                .andDo(
                    document(
                        "AdminTemplateApi/createTemplate",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                            fieldWithPath("isSuccess").description("성공 여부"),
                            fieldWithPath("code").description("응답 코드"),
                            fieldWithPath("message").description("응답 메시지"),
                            fieldWithPath("result").description("반환 결과"),
                            fieldWithPath("result.templateId").description("템플릿 ID")
                        )
                    )
                );
        }

        @Nested
        @WithMockCustomUser(username = "admin", role = Role.ADMIN)
        @DisplayName("관리자 템플릿 수정 API [PATCH /api/admin/templates/{categoryId}]")
        class UpdateTemplate {

            @Test
            @DisplayName("템플릿 수정에 성공한다.")
            void updateTemplate_Success() throws Exception {
                // given
                AdminTemplateUpdateRequest request = new AdminTemplateUpdateRequest("수정된 내용");
                TemplatePKResponse response = new TemplatePKResponse(PkCrypto.encrypt(TEMPLATE_ID));
                given(templateUpdateUseCase.updateTemplate(CATEGORY_ID, request)).willReturn(response);

                // when & then
                mockMvc.perform(patch("/api/admin/templates/{categoryId}", PkCrypto.encrypt(CATEGORY_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.templateId").value(PkCrypto.encrypt(TEMPLATE_ID)))
                    .andDo(
                        document(
                            "AdminTemplateApi/updateTemplate",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                parameterWithName("categoryId").description("수정할 템플릿의 카테고리 ID")
                            ),
                            responseFields(
                                fieldWithPath("isSuccess").description("성공 여부"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("반환 결과"),
                                fieldWithPath("result.templateId").description("템플릿 ID")
                            )
                        )
                    );
            }
        }

        @Nested
        @WithMockCustomUser(username = "admin", role = Role.ADMIN)
        @DisplayName("관리자 템플릿 삭제 API [DELETE /api/admin/templates/{templateId}]")
        class DeleteTemplate {

            @Test
            @DisplayName("템플릿 삭제에 성공한다.")
            void deleteTemplate_Success() throws Exception {
                // given
                TemplatePKResponse response = new TemplatePKResponse(PkCrypto.encrypt(TEMPLATE_ID));
                given(templateDeleteUseCase.deleteTemplate(anyLong())).willReturn(response);

                // when
                mockMvc.perform(delete("/api/admin/templates/{templateId}", PkCrypto.encrypt(TEMPLATE_ID)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.templateId").value(PkCrypto.encrypt(TEMPLATE_ID)))
                    .andDo(
                        document(
                            "AdminTemplateApi/deleteTemplate",
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                parameterWithName("templateId").description("삭제할 템플릿의 ID")
                            ),
                            responseFields(
                                fieldWithPath("isSuccess").description("성공 여부"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("반환 결과"),
                                fieldWithPath("result.templateId").description("템플릿 ID")
                            )
                        )
                    );
            }
        }
    }
}