package com.wrkr.tickety;

import com.wrkr.tickety.domains.ticket.application.dto.request.GuideUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.GuideMapper;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideDeleteUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideUpdateUseCase;
import com.wrkr.tickety.domains.ticket.domain.service.Guide.GuideCreateService;
import com.wrkr.tickety.domains.ticket.domain.service.Guide.GuideDeleteService;
import com.wrkr.tickety.domains.ticket.domain.service.Guide.GuideGetService;
import com.wrkr.tickety.domains.ticket.domain.service.Guide.GuideUpdateService;
import com.wrkr.tickety.domains.ticket.persistence.repository.CategoryRepository;
import com.wrkr.tickety.domains.ticket.persistence.repository.GuideRepository;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.ApplicationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GuideUseCaseTest {

    private static final Logger log = LoggerFactory.getLogger(GuideUseCaseTest.class);
    @Mock
    private GuideRepository guideRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private GuideMapper guideMapper;

    @Mock
    private GuideUpdateService guideUpdateService;

    @Mock
    private GuideCreateService guideCreateService;

    @Mock
    private GuideDeleteService guideDeleteService;

    @Mock
    private GuideGetService guideGetService;

    @InjectMocks
    private GuideGetUseCase guideGetUseCase;

    @InjectMocks
    private GuideUpdateUseCase guideUpdateUseCase;

    @InjectMocks
    private GuideCreateUseCase guideCreateUseCase;

    @InjectMocks
    private GuideDeleteUseCase guideDeleteUseCase;

    @Test
    @DisplayName("카테고리id를 이용해 도움말을 조회한다.")
    void testGetGuide() throws ApplicationException {
        // given
        String categoryId = "1";

        GuideResponse guideResponseByCategory = GuideResponse.builder()
                .categoryId("1")
                .guideId("1")
                .content("테스트 도움말")
                .build();

        given(guideGetService.getGuideContentByCategory(categoryId)).willReturn(guideResponseByCategory);

        // when
        ResponseEntity<ApplicationResponse<GuideResponse>> response = guideGetUseCase.getGuide(categoryId);

        // then
        assertNotNull(response.getBody());
        assertEquals("SUCCESS", response.getBody().getCode());
        assertEquals(guideResponseByCategory, response.getBody().getResult());
    }

//    @Test
//    @DisplayName("도움말을 생성한다.")
//    void createGuideTest() {
//
//        // given
//        Category category = Category.builder().build();
//        String cryptoCategoryId = "mockedEncryptedValue";
//
//        GuideCreateRequest guideCreateRequest = GuideCreateRequest.builder()
//                .categoryId("1")
//                .content("테스트 도움말")
//                .build();
//
//        GuideResponse guideResponse = GuideResponse.builder()
//                .guideId("1")
//                .content("테스트 도움말")
//                .categoryId("1")
//                .build();
//
//        given(guideCreateService.createGuide(guideCreateRequest, category)).willReturn(guideResponse);
//        given(categoryRepository.findById(PkCrypto.decrypt(cryptoCategoryId))).willReturn(Optional.of(category));
//        // when
//        when(pkCrypto.encryptValue(1L)).thenReturn("mockedEncryptedValue");
//        when(pkCrypto.decryptValue("mockedEncryptedValue")).thenReturn(1L);
//        ResponseEntity<ApplicationResponse<GuideResponse>> response = guideCreateUseCase.createGuide(guideCreateRequest, cryptoCategoryId);
//
//        // then
//        assertNotNull(response.getBody());
//        assertEquals("SUCCESS", response.getBody().getCode());
//        assertEquals(guideResponse, response.getBody().getResult());
//    }

    @Test
    @DisplayName("도움말의 내용을 수정한다.")
    void updateGuideTest() {
        // given
        String guideId = "1";

        GuideResponse guideResponse = GuideResponse.builder()
                .guideId("1")
                .content("수정된 도움말")
                .categoryId("1")
                .build();

        GuideUpdateRequest guideUpdateRequest = GuideUpdateRequest.builder()
                .content("수정된 도움말")
                .build();
        given(guideUpdateService.modifyGuide(guideId, guideUpdateRequest)).willReturn(guideResponse);
        // when
        ResponseEntity<ApplicationResponse<GuideResponse>> response = guideUpdateUseCase.modifyGuide(guideId, guideUpdateRequest);

        // then
        assertNotNull(response.getBody());
        assertEquals("SUCCESS", response.getBody().getCode());
        assertEquals(guideResponse, response.getBody().getResult());
    }

    @Test
    @DisplayName("도움말을 삭제한다. 유스케이스 계츠을 테스트한다.")
    void deleteGuideTestInUseCase() {
        // given
        String guideId = "1";

        // when
        guideDeleteUseCase.deleteGuide(guideId);

        // then
        verify(guideDeleteService, times(1)).deleteGuide(guideId);
    }
}
