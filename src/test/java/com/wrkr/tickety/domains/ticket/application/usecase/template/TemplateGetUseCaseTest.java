package com.wrkr.tickety.domains.ticket.application.usecase.template;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplateGetResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TemplateMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateGetService;
import com.wrkr.tickety.domains.ticket.exception.TemplateErrorCode;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TemplateGetUseCaseTest {

    @InjectMocks
    private TemplateGetUseCase templateGetUseCase;

    @Mock
    private TemplateGetService templateGetService;

    private static final Long CATEGORY_ID = 1L;
    private static final Long TEMPLATE_ID = 1L;
    private static final Long WRONG_CATEGORY_ID = 2L;
    private static final String TEMPLATE_CONTENT = "템플릿 내용";

    private Category category;

    @BeforeEach
    void setUp() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();

        category = Category.builder()
            .categoryId(CATEGORY_ID)
            .name("카테고리")
            .abbreviation("CT")
            .seq(1)
            .parent(null)
            .isDeleted(false)
            .build();
    }

    @Test
    @DisplayName("템플릿을 조회하면, 지정된 반환값으로 템플릿을 반환한다.")
    void getTemplate_Success() {
        //given
        Template template = Template.builder()
            .templateId(TEMPLATE_ID)
            .category(category)
            .content(TEMPLATE_CONTENT)
            .build();

        given(templateGetService.getTemplateByCategoryId(CATEGORY_ID)).willReturn(template);

        TemplateGetResponse expectedResponse = TemplateGetResponse.builder()
            .templateId(PkCrypto.encrypt(TEMPLATE_ID))
            .categoryId(PkCrypto.encrypt(CATEGORY_ID))
            .content(TEMPLATE_CONTENT)
            .build();

        try (MockedStatic<TemplateMapper> mockedMapper = mockStatic(TemplateMapper.class)) {
            mockedMapper.when(() -> TemplateMapper.mapToAdminTemplateGetResponse(template))
                .thenReturn(TemplateGetResponse.builder()
                    .templateId(PkCrypto.encrypt(template.getTemplateId()))
                    .categoryId(PkCrypto.encrypt(template.getCategory().getCategoryId()))
                    .content(template.getContent())
                    .build());

            //when
            TemplateGetResponse templateGetResponse = templateGetUseCase.getTemplate(CATEGORY_ID);

            //then
            assertThat(templateGetResponse).isEqualTo(expectedResponse);
        }
    }

    @Test
    @DisplayName("템플릿이 존재하지 않는 카테고리 PK로 템플릿을 조회하면, 예외를 던진다.")
    void getTemplate_TemplateNotExists() {
        //given
        given(templateGetService.getTemplateByCategoryId(WRONG_CATEGORY_ID))
            .willThrow(ApplicationException.from(TemplateErrorCode.TEMPLATE_NOT_EXISTS));

        //when & then
        assertThatThrownBy(() -> templateGetUseCase.getTemplate(WRONG_CATEGORY_ID))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TemplateErrorCode.TEMPLATE_NOT_EXISTS.getMessage());
    }
}