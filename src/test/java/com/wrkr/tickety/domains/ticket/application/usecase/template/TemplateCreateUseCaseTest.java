package com.wrkr.tickety.domains.ticket.application.usecase.template;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

import com.wrkr.tickety.domains.ticket.application.dto.request.template.AdminTemplateCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.template.TemplatePKResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.TemplateMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateCreateService;
import com.wrkr.tickety.domains.ticket.domain.service.template.TemplateGetService;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
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
class TemplateCreateUseCaseTest {

    @InjectMocks
    private TemplateCreateUseCase templateCreateUseCase;

    @Mock
    private TemplateCreateService templateCreateService;

    @Mock
    private TemplateGetService templateGetService;

    @Mock
    private CategoryGetService categoryGetService;

    private static final Long CATEGORY_ID = 1L;
    private static final Long WRONG_CATEGORY_ID = 2L;
    private static final Long TEMPLATE_ID = 1L;

    private Category parentCategory;
    private AdminTemplateCreateRequest validRequest;

    @BeforeEach
    void setUp() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();

        parentCategory = Category.builder()
            .categoryId(CATEGORY_ID)
            .name("부모 카테고리")
            .parent(null)
            .seq(1)
            .isDeleted(false)
            .abbreviation("PC")
            .build();

        validRequest = new AdminTemplateCreateRequest(
            PkCrypto.encrypt(CATEGORY_ID),
            "템플릿 내용"
        );

    }

    @Test
    @DisplayName("템플릿을 생성할 시, 템플릿의 PK를 전송한다.")
    void createTemplate_Success() {
        //given
        given(categoryGetService.getParentCategory(PkCrypto.decrypt(validRequest.categoryId())))
            .willReturn(parentCategory);
        given(templateGetService.existsByCategoryId(CATEGORY_ID))
            .willReturn(false);

        Template requestTemplate = Template.builder()
            .category(parentCategory)
            .content(validRequest.content())
            .build();

        Template createdTemplate = Template.builder()
            .templateId(TEMPLATE_ID)
            .category(parentCategory)
            .content(validRequest.content())
            .build();

        try (MockedStatic<TemplateMapper> mockedMapper = mockStatic(TemplateMapper.class)) {
            mockedMapper.when(() -> TemplateMapper.mapToTemplateDomain(validRequest, parentCategory))
                .thenReturn(requestTemplate);

            mockedMapper.when(() -> TemplateMapper.mapToTemplatePKResponse(createdTemplate))
                .thenReturn(new TemplatePKResponse(PkCrypto.encrypt(TEMPLATE_ID)));

            given(templateCreateService.save(requestTemplate)).willReturn(createdTemplate);

            // when
            TemplatePKResponse response = templateCreateUseCase.createTemplate(validRequest);

            // then
            assertThat(response).isEqualTo(new TemplatePKResponse(PkCrypto.encrypt(TEMPLATE_ID)));
        }
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 ID로 생성 시, 예외가 발생한다.")
    void createTemplate_CategoryNotFound() {
        // given
        AdminTemplateCreateRequest wrongValidRequest = new AdminTemplateCreateRequest(
            PkCrypto.encrypt(WRONG_CATEGORY_ID),
            "템플릿 내용"
        );

        given(categoryGetService.getParentCategory(PkCrypto.decrypt(wrongValidRequest.categoryId())))
            .willThrow(ApplicationException.from(CategoryErrorCode.CATEGORY_NOT_EXISTS));

        // when & then
        assertThatThrownBy(() -> templateCreateUseCase.createTemplate(wrongValidRequest))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(CategoryErrorCode.CATEGORY_NOT_EXISTS.getMessage());
    }

    @Test
    @DisplayName("템플릿이 이미 존재하는 카테고리 ID로 생성 시, 예외가 발생한다.")
    void createTemplate_TemplateAlreadyExists() {
        //given
        given(categoryGetService.getParentCategory(PkCrypto.decrypt(validRequest.categoryId())))
            .willReturn(parentCategory);
        given(templateGetService.existsByCategoryId(CATEGORY_ID))
            .willReturn(true);

        // when & then
        assertThatThrownBy(() -> templateCreateUseCase.createTemplate(validRequest))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(TemplateErrorCode.TEMPLATE_ALREADY_EXISTS.getMessage());

    }
}