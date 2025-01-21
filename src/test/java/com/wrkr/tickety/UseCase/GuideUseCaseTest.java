package com.wrkr.tickety.UseCase;

import com.wrkr.tickety.domains.ticket.application.dto.request.GuideCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.GuideUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.GuideMapper;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideDeleteUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideUpdateUseCase;
import com.wrkr.tickety.domains.ticket.domain.GuideDomain;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.Guide.GuideCreateService;
import com.wrkr.tickety.domains.ticket.domain.service.Guide.GuideDeleteService;
import com.wrkr.tickety.domains.ticket.domain.service.Guide.GuideGetService;
import com.wrkr.tickety.domains.ticket.domain.service.Guide.GuideUpdateService;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GuideUseCaseTest {

    @Mock
    private GuideMapper guideMapper;

    @Mock
    private GuideUpdateService guideUpdateService;

    @Mock
    private GuideCreateService guideCreateService;

    @Mock
    private GuideDeleteService guideDeleteService;

    @Mock
    private CategoryGetService categoryGetService;

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

    @Mock
    private PkCrypto pkCrypto;

    @BeforeEach
    public void setUp() {
        // 필요한 값으로 PkCrypto 객체 초기화
        pkCrypto = new PkCrypto("AES", "GREATTEAM4WRKR12");
        pkCrypto.init(); // @PostConstruct가 동작하지 않으므로 명시적으로 호출
    }

    @Test
    @DisplayName("카테고리id를 이용해 도움말을 조회한다.")
    void testGetGuide() throws ApplicationException {
        // given
        String categoryId = "1";

        GuideDomain guideDomain = GuideDomain.builder()
                .guideId(1L)
                .content("테스트 도움말")
                .build();

        GuideResponse guideResponseByCategory = GuideResponse.builder()
                .content("테스트 도움말")
                .guideId("W1NMMfAHGTnNGLdRL3lvcw==")
                .build();


        given(guideGetService.getGuideContentByCategory(categoryId)).willReturn(guideDomain);
        given(guideMapper.guideToGuideResponse(guideDomain)).willReturn(guideResponseByCategory);
        // when
        GuideResponse response = guideGetUseCase.getGuide(categoryId);

        // then
        assertNotNull(response.guideId());
        assertEquals("W1NMMfAHGTnNGLdRL3lvcw==", response.guideId());
        assertEquals(guideDomain.getContent(), response.content());
    }

    @Test
    @DisplayName("도움말을 생성한다.")
    void createGuideTest() {
        //given
        long categoryId = 1L;
        String cryptoCategoryId = pkCrypto.encryptValue(categoryId);
        Category category = new Category(null, "test", 1);
        GuideDomain guideDomain = GuideDomain.builder()
                .content("test")
                .categoryId(categoryId)
                .build();
        GuideCreateRequest guideCreateRequest = GuideCreateRequest.builder()
                .content("test")
                .build();

        given(categoryGetService.getCategory(guideDomain.getCategoryId())).willReturn(Optional.of(category));
        given(guideCreateService.createGuide(any(GuideDomain.class), eq(category)))
                .willReturn(guideDomain);
        given(guideMapper.guideIdToPkResponse(guideDomain)).willReturn(new PkResponse(cryptoCategoryId));
        //when
        PkResponse response = guideCreateUseCase.createGuide(guideCreateRequest, cryptoCategoryId);

        //then
        assertEquals(cryptoCategoryId, response.id());
    }

    @Test
    @DisplayName("도움말의 내용을 수정한다.")
    void updateGuideTest() {
        // given
        long guideId = 1L;
        String cryptoGuideId = pkCrypto.encryptValue(guideId);
        GuideDomain guideDomain = GuideDomain.builder()
                .guideId(1L)
                .content("수정된 도움말")
                .build();

        GuideUpdateRequest guideUpdateRequest = GuideUpdateRequest.builder()
                .content("수정된 도움말")
                .build();
        given(guideUpdateService.updateGuide(cryptoGuideId, guideUpdateRequest)).willReturn(guideDomain);
        given(guideMapper.guideIdToPkResponse(guideDomain)).willReturn(new PkResponse(cryptoGuideId));
        // when
        PkResponse response = guideUpdateUseCase.modifyGuide(cryptoGuideId, guideUpdateRequest);

        // then
        assertEquals(cryptoGuideId, response.id());
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
