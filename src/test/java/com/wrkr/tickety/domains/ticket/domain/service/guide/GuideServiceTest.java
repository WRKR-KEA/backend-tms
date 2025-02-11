package com.wrkr.tickety.domains.ticket.domain.service.guide;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wrkr.tickety.domains.ticket.application.dto.request.GuideUpdateRequest;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.adapter.GuidePersistenceAdapter;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GuideServiceTest {

    @Mock
    private GuidePersistenceAdapter guidePersistenceAdapter;

    @InjectMocks
    private GuideCreateService guideCreateService;

    @InjectMocks
    private GuideDeleteService guideDeleteService;

    @InjectMocks
    private GuideGetService guideGetService;

    @InjectMocks
    private GuideUpdateService guideUpdateService;

    @Mock
    private PkCrypto pkCrypto;


    @BeforeEach
    void setup() {
        pkCrypto = new PkCrypto("AES", "GREATTEAM4WRKR12");
        pkCrypto.init();
    }


    @Test
    @DisplayName("도움말을 생성한다.")
    void createGuide() {
        // Given
        Guide guide = Guide.builder()
            .category(Category.builder().categoryId(1L).build())
            .build();
        // When
        when(guidePersistenceAdapter.existsByCategoryId(guide.getCategory().getCategoryId())).thenReturn(false);
        when(guidePersistenceAdapter.save(guide)).thenReturn(guide);
        // Then
        Guide createdGuide = guideCreateService.createGuide(guide);

        assertEquals(guide, createdGuide);
    }

    @Test
    @DisplayName("이미 도움말이 존재하는 카테고리에 도움말을 생성하려고 하면 예외가 발생한다.")
    void createGuideWithAlreadyExistCategory() {
        // Given
        Guide guide = Guide.builder()
            .category(Category.builder().categoryId(1L).build())
            .build();
        // When
        when(guidePersistenceAdapter.existsByCategoryId(guide.getCategory().getCategoryId())).thenReturn(true);
        // Then
        assertThrows(ApplicationException.class, () -> guideCreateService.createGuide(guide));
    }

    @Test
    @DisplayName("도움말을 삭제한다.")
    void deleteGuide() {
        // Given
        Long guideId = 1L;
        Guide guide = Guide.builder()
            .guideId(guideId)
            .build();
        // When
        when(guidePersistenceAdapter.findById(guideId)).thenReturn(Optional.ofNullable(guide));
        guideDeleteService.deleteGuide(guideId);
        // Then
        verify(guidePersistenceAdapter).deleteById(guide.getGuideId());
    }

    @Test
    @DisplayName("존재하지 않는 도움말을 삭제하려고 하면 예외가 발생한다.")
    void deleteGuideWithNotExistGuide() {
        // Given
        Long guideId = 1L;
        // When
        when(guidePersistenceAdapter.findById(guideId)).thenReturn(Optional.empty());
        // Then
        assertThrows(ApplicationException.class, () -> guideDeleteService.deleteGuide(guideId));
    }

    @Test
    @DisplayName("도움말을 조회한다.")
    void getGuide() {
        // Given
        Long categoryId = 1L;
        Guide guide = Guide.builder()
            .category(Category.builder().categoryId(categoryId).build())
            .build();
        // When
        when(guidePersistenceAdapter.findByCategoryId(categoryId)).thenReturn(Optional.ofNullable(guide));
        // Then
        Guide foundGuide = guideGetService.getGuideContentByCategory(categoryId);

        assertEquals(guide, foundGuide);
    }

    @Test
    @DisplayName("존재하지 않는 도움말을 조회하려고 하면 예외가 발생한다.")
    void getGuideWithNotExistGuide() {
        // Given
        Long categoryId = 1L;
        // When
        when(guidePersistenceAdapter.findByCategoryId(categoryId)).thenReturn(Optional.empty());
        // Then
        assertThrows(ApplicationException.class, () -> guideGetService.getGuideContentByCategory(categoryId));
    }

    @Test
    @DisplayName("도움말을 수정한다.")
    void updateGuide() {
        // Given
        Long guideId = 1L;
        Guide guide = Guide.builder()
            .guideId(guideId)
            .build();

        Guide updatedGuideVerify = Guide.builder()
            .guideId(guideId)
            .content("updated content")
            .build();
        // When
        when(guidePersistenceAdapter.findById(guideId)).thenReturn(Optional.ofNullable(guide));
        when(guidePersistenceAdapter.save(guide)).thenReturn(guide);
        // Then
        Guide updatedGuide = guideUpdateService.updateGuide(guideId,
            GuideUpdateRequest.builder().content("updated content").build()
        );

        assertEquals(updatedGuideVerify.getContent(), updatedGuide.getContent());
        verify(guidePersistenceAdapter).save(guide);
    }

    @Test
    @DisplayName("존재하지 않는 도움말을 수정하려고 하면 예외가 발생한다.")
    void updateGuideWithNotExistGuide() {
        // Given
        Long guideId = 1L;
        // When
        when(guidePersistenceAdapter.findById(guideId)).thenReturn(Optional.empty());
        // Then
        ApplicationException e = assertThrows(ApplicationException.class, () -> guideUpdateService.updateGuide(guideId,
            GuideUpdateRequest.builder().content("updated content").build()
        ));
        assertEquals(GuideErrorCode.GUIDE_NOT_EXIST, e.getCode());
    }
}
