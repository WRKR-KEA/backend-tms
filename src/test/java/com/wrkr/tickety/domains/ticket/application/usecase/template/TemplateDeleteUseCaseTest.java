package com.wrkr.tickety.domains.ticket.application.usecase.template;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplatePKResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TemplateMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateDeleteService;
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
class TemplateDeleteUseCaseTest {

    @InjectMocks
    private TemplateDeleteUseCase templateDeleteUseCase;

    @Mock
    private TemplateGetService templateGetService;

    @Mock
    private TemplateDeleteService templateDeleteService;

    private static final Long TEMPLATE_ID = 1L;
    private static final Long WRONG_TEMPLATE_ID = 2L;

    @BeforeEach
    void setUp() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }


    @Test
    @DisplayName("템플릿 삭제 시, 삭제한 템플릿의 PK를 반환한다.")
    void deleteTemplate_Success() {
        // given
        String requestedTemplateId = PkCrypto.encrypt(TEMPLATE_ID);

        Category category = Category.builder()
            .categoryId(1L)
            .name("카테고리")
            .parent(null)
            .seq(1)
            .isDeleted(false)
            .abbreviation("CG")
            .build();

        Template template = Template.builder()
            .templateId(TEMPLATE_ID)
            .category(category)
            .content("템플릿 내용")
            .build();

        given(templateGetService.getTemplateByTemplateId(PkCrypto.decrypt(requestedTemplateId))).willReturn(template);
        given(templateDeleteService.deleteTemplate(template)).willReturn(template);

        try (MockedStatic<TemplateMapper> mockedMapper = mockStatic(TemplateMapper.class)) {
            mockedMapper.when(() -> TemplateMapper.mapToTemplatePKResponse(template))
                .thenReturn(TemplatePKResponse.builder().templateId(PkCrypto.encrypt(template.getTemplateId())).build());

            // when
            TemplatePKResponse response = templateDeleteUseCase.deleteTemplate(PkCrypto.decrypt(requestedTemplateId));

            // then
            assertThat(response).isEqualTo(new TemplatePKResponse(PkCrypto.encrypt(template.getTemplateId())));
        }
    }

    @Test
    @DisplayName("템플릿 삭제 시, 삭제할 템플릿이 존재하지 않으면 예외를 발생시킨다.")
    void deleteTemplate_TemplateNotExists() {
        // given
        String requestedTemplateId = PkCrypto.encrypt(WRONG_TEMPLATE_ID);

        given(templateGetService.getTemplateByTemplateId(PkCrypto.decrypt(requestedTemplateId)))
            .willThrow(ApplicationException.from(TemplateErrorCode.TEMPLATE_NOT_EXISTS));

        // when & then
        assertThatThrownBy(() -> templateDeleteUseCase.deleteTemplate(PkCrypto.decrypt(requestedTemplateId)))
            .isInstanceOf(ApplicationException.class)
            .hasMessageContaining(TemplateErrorCode.TEMPLATE_NOT_EXISTS.getMessage());
    }
}