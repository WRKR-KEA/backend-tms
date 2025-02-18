package com.wrkr.tickety.domains.ticket.application.usecase.template;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

import com.wrkr.tickety.domains.ticket.application.dto.request.template.AdminTemplateUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplatePKResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TemplateMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateGetService;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateUpdateService;
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
class TemplateUpdateUseCaseTest {

    @InjectMocks
    private TemplateUpdateUseCase templateUpdateUseCase;

    @Mock
    private TemplateGetService templateGetService;

    @Mock
    private TemplateUpdateService templateUpdateService;

    private static final Long CATEGORY_ID = 1L;
    private static final Long WRONG_CATEGORY_ID = 2L;
    private static final Long TEMPLATE_ID = 1L;

    private Category category;
    private Template template;
    private AdminTemplateUpdateRequest validRequest;

    @BeforeEach
    void setUp() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();

        category = Category.builder()
            .categoryId(CATEGORY_ID)
            .name("카테고리")
            .parent(null)
            .seq(1)
            .isDeleted(false)
            .abbreviation("CG")
            .build();

        template = Template.builder()
            .templateId(1L)
            .category(category)
            .content("템플릿1 내용")
            .build();

        validRequest = new AdminTemplateUpdateRequest("템플릿1 수정된 내용");

    }


    @Test
    @DisplayName("템플릿을 수정할 시, 템플릿의 PK를 전송한다.")
    void updateTemplate_Success() {
        //given
        String requestedCategoryId = PkCrypto.encrypt(CATEGORY_ID);
        
        given(templateGetService.getTemplateByCategoryId(PkCrypto.decrypt(requestedCategoryId)))
            .willReturn(template);

        Template updatedTemplate = Template.builder()
            .templateId(TEMPLATE_ID)
            .category(category)
            .content(validRequest.content())
            .build();

        given(templateUpdateService.update(template, validRequest)).willReturn(updatedTemplate);

        try (MockedStatic<TemplateMapper> mockedMapper = mockStatic(TemplateMapper.class)) {
            mockedMapper.when(() -> TemplateMapper.mapToTemplatePKResponse(updatedTemplate))
                .thenReturn(TemplatePKResponse.builder().templateId(PkCrypto.encrypt(updatedTemplate.getTemplateId())).build());

            //when
            TemplatePKResponse response = templateUpdateUseCase.updateTemplate(PkCrypto.decrypt(requestedCategoryId), validRequest);

            //then
            assertThat(response).isEqualTo(new TemplatePKResponse(PkCrypto.encrypt(updatedTemplate.getTemplateId())));
        }
    }

    @Test
    @DisplayName("템플릿을 수정할 시, 템플릿이 존재하지 않으면 예외를 발생시킨다.")
    void updateTemplate_TemplateNotExists() {
        //given
        String requestedCategoryId = PkCrypto.encrypt(WRONG_CATEGORY_ID);
        given(templateGetService.getTemplateByCategoryId(PkCrypto.decrypt(requestedCategoryId)))
            .willThrow(ApplicationException.from(TemplateErrorCode.TEMPLATE_ALREADY_EXISTS));

        //when & then
        assertThatThrownBy(() -> templateUpdateUseCase.updateTemplate(PkCrypto.decrypt(requestedCategoryId), validRequest))
            .isInstanceOf(ApplicationException.class)
            .hasMessageContaining(TemplateErrorCode.TEMPLATE_ALREADY_EXISTS.getMessage());
    }

}