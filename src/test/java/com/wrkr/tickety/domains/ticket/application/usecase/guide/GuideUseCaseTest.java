package com.wrkr.tickety.domains.ticket.application.usecase.guide;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wrkr.tickety.domains.attachment.domain.service.GuideAttachmentGetService;
import com.wrkr.tickety.domains.attachment.domain.service.GuideAttachmentUploadService;
import com.wrkr.tickety.domains.attachment.domain.service.S3ApiService;
import com.wrkr.tickety.domains.ticket.application.dto.request.GuideCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.GuideUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.GuideMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideCreateService;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideDeleteService;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideGetService;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideUpdateService;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.adapter.CategoryPersistenceAdapter;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GuideUseCaseTest {

    @Mock
    private GuideMapper guideMapper;

    @Mock
    private CategoryPersistenceAdapter categoryPersistenceAdapter;

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

    @Mock
    private GuideAttachmentGetService guideAttachmentGetService;

    @Mock
    private S3ApiService s3ApiService;

    @Mock
    private GuideAttachmentUploadService guideAttachmentUploadService;

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
        // Given
        Long categoryId = 1L;

        Guide guideDomain = Guide.builder()
            .guideId(1L)
            .content("테스트 도움말")
            .build();

        GuideResponse guideResponseByCategory = GuideResponse.builder()
            .content("테스트 도움말")
            .guideId("W1NMMfAHGTnNGLdRL3lvcw==")
            .build();

        given(guideGetService.getGuideContentByCategory(categoryId)).willReturn(guideDomain);
        given(guideMapper.guideToGuideResponse(guideDomain, guideAttachmentGetService)).willReturn(guideResponseByCategory);

        // When
        GuideResponse response = guideGetUseCase.getGuide(categoryId);

        // Then
        assertNotNull(response.guideId());
        assertEquals("W1NMMfAHGTnNGLdRL3lvcw==", response.guideId());
        assertEquals(guideDomain.getContent(), response.content());
    }

    @Test
    @DisplayName("존재하지 않는 카테고리id로 도움말을 조회하면 예외가 발생한다.")
    void testGetGuideWithNonExistCategory() {
        // Given
        Long categoryId = 1L;
        when(guideGetService.getGuideContentByCategory(categoryId)).thenThrow(ApplicationException.from(CategoryErrorCode.CATEGORY_NOT_EXISTS));
        // When
        ApplicationException e = assertThrows(ApplicationException.class, () -> guideGetUseCase.getGuide(categoryId));
        // Then
        assertEquals(CategoryErrorCode.CATEGORY_NOT_EXISTS, e.getCode());

    }

    @Test
    @DisplayName("도움말을 생성한다.")
    void createGuideTest() {
        // Given
        long parentId = 1L;
        long categoryId = 2L;
        String cryptoCategoryId = pkCrypto.encryptValue(categoryId);
        Category parent = Category.builder()
            .categoryId(parentId)
            .name("카테고리 1")
            .seq(1)
            .isDeleted(false)
            .build();
        Category category = Category.builder()
            .categoryId(categoryId)
            .parent(parent)
            .name("카테고리 2")
            .seq(2)
            .isDeleted(false)
            .build();

        Guide guide = Guide.builder()
            .content("test")
            .category(category)
            .build();
        GuideCreateRequest guideCreateRequest = GuideCreateRequest.builder()
            .content("test")
            .build();

        MockMultipartFile file = new MockMultipartFile(
            "attachments",  // 필드명 (컨트롤러에서 받을 @RequestPart 이름과 일치해야 함)
            "test.png",     // 파일명
            "text/plain",   // MIME 타입
            "테스트 파일 내용입니다.".getBytes(StandardCharsets.UTF_8) // 파일 내용
        );
        String url = "this is file url";

        List<MultipartFile> attachments = new ArrayList<>();
        attachments.add(file);

        given(s3ApiService.uploadGuideFile(file)).willReturn(url);
        given(categoryGetService.getParentCategory(categoryId)).willReturn(category);
        given(guideCreateService.createGuide(any(Guide.class))).willReturn(guide);
        given(guideMapper.guideIdToPkResponse(guide)).willReturn(
            PkResponse.builder().id(cryptoCategoryId).build());

        // When
        PkResponse response = guideCreateUseCase.createGuide(guideCreateRequest, categoryId, attachments);

        // Then
        assertEquals(cryptoCategoryId, response.id());
    }

    @Test
    @DisplayName("카테고리에 대한 도움말이 이미 존재할 때 도움말을 생성하면 예외가 발생한다.")
    void testCreateGuideWithExistingGuide() {
        // Given
        long categoryId = 1L;
        Category category = Category.builder()
            .categoryId(categoryId)
            .name("카테고리 1")
            .seq(1)
            .isDeleted(false)
            .build();
        GuideCreateRequest guideCreateRequest = GuideCreateRequest.builder()
            .content("test")
            .build();

        MockMultipartFile file = new MockMultipartFile(
            "attachments",  // 필드명 (컨트롤러에서 받을 @RequestPart 이름과 일치해야 함)
            "test.txt",     // 파일명
            "text/plain",   // MIME 타입
            "테스트 파일 내용입니다.".getBytes(StandardCharsets.UTF_8) // 파일 내용
        );

        List<MultipartFile> attachments = new ArrayList<>();
        attachments.add(file);
        when(categoryGetService.getParentCategory(categoryId)).thenReturn(category);
        when(guideCreateService.createGuide(any(Guide.class))).thenThrow(ApplicationException.from(GuideErrorCode.GUIDE_ALREADY_EXIST));
        // When
        ApplicationException e = assertThrows(ApplicationException.class, () -> guideCreateUseCase.createGuide(guideCreateRequest, categoryId, attachments));
        // Then
        assertEquals(GuideErrorCode.GUIDE_ALREADY_EXIST, e.getCode());
    }

    @Test
    @DisplayName("도움말의 내용을 수정한다.")
    void updateGuideTest() {
        // Given
        long guideId = 1L;
        String cryptoGuideId = pkCrypto.encryptValue(guideId);
        Guide guideDomain = Guide.builder()
            .guideId(1L)
            .content("수정된 도움말")
            .build();

        GuideUpdateRequest guideUpdateRequest = GuideUpdateRequest.builder()
            .content("수정된 도움말")
            .build();

        MockMultipartFile file = new MockMultipartFile(
            "attachments",  // 필드명 (컨트롤러에서 받을 @RequestPart 이름과 일치해야 함)
            "test.jpg",     // 파일명
            "text/plain",   // MIME 타입
            "테스트 파일 내용입니다.".getBytes(StandardCharsets.UTF_8) // 파일 내용
        );

        List<MultipartFile> attachments = new ArrayList<>();
        attachments.add(file);
        given(guideUpdateService.updateGuide(guideId, guideUpdateRequest)).willReturn(
            guideDomain);
        given(guideMapper.guideIdToPkResponse(guideDomain)).willReturn(
            new PkResponse(cryptoGuideId));

        // When
        PkResponse response = guideUpdateUseCase.modifyGuide(guideId, guideUpdateRequest, attachments);

        // Then
        assertEquals(cryptoGuideId, response.id());
    }

    @Test
    @DisplayName("존재하지 않는 도움말을 수정하면 예외가 발생한다.")
    void testUpdateGuideWithNonExistGuide() {
        // Given
        long guideId = 1L;
        GuideUpdateRequest guideUpdateRequest = GuideUpdateRequest.builder()
            .content("수정된 도움말")
            .build();
        MockMultipartFile file = new MockMultipartFile(
            "attachments",  // 필드명 (컨트롤러에서 받을 @RequestPart 이름과 일치해야 함)
            "test.txt",     // 파일명
            "text/plain",   // MIME 타입
            "테스트 파일 내용입니다.".getBytes(StandardCharsets.UTF_8) // 파일 내용
        );

        List<MultipartFile> attachments = new ArrayList<>();
        attachments.add(file);
        when(guideUpdateService.updateGuide(guideId, guideUpdateRequest)).thenThrow(ApplicationException.from(GuideErrorCode.GUIDE_NOT_EXIST));
        // When
        ApplicationException e = assertThrows(ApplicationException.class, () -> guideUpdateUseCase.modifyGuide(guideId, guideUpdateRequest, attachments));
        // Then
        assertEquals(GuideErrorCode.GUIDE_NOT_EXIST, e.getCode());
    }

    @Test
    @DisplayName("도움말을 삭제한다.")
    void deleteGuideTestInUseCase() {
        // Given
        Long guideId = 1L;

        // When
        guideDeleteUseCase.deleteGuide(guideId);

        // Then
        verify(guideDeleteService, times(1)).deleteGuide(guideId);
    }

    @Test
    @DisplayName("존재하지 않는 도움말을 삭제하면 예외가 발생한다.")
    void testDeleteGuideWithNonExistGuide() {
        // Given
        Long guideId = 1L;
        when(guideDeleteService.deleteGuide(guideId)).thenThrow(ApplicationException.from(GuideErrorCode.GUIDE_NOT_EXIST));
        // When
        ApplicationException e = assertThrows(ApplicationException.class, () -> guideDeleteUseCase.deleteGuide(guideId));
        // Then
        assertEquals(GuideErrorCode.GUIDE_NOT_EXIST, e.getCode());
    }
}
