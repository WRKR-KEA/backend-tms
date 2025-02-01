package com.wrkr.tickety.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.persistence.adapter.GuidePersistenceAdapter;
import com.wrkr.tickety.domains.ticket.persistence.entity.GuideEntity;
import com.wrkr.tickety.domains.ticket.persistence.mapper.GuidePersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.repository.GuideRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GuideAdapterTest {

    @InjectMocks
    private GuidePersistenceAdapter guidePersistenceAdapter;

    @Mock
    private GuideRepository guideRepository;

    @Mock
    private GuidePersistenceMapper guidePersistenceMapper;

    @Test
    @DisplayName("도움말을 저장한다.")
    void saveTest() {
        //given
        GuideEntity guideEntity = GuideEntity.builder()
            .guideId(1L)
            .build();
        Guide guide = Guide.builder()
            .guideId(1L)
            .build();
        when(guidePersistenceMapper.toEntity(guide)).thenReturn(guideEntity);
        when(guideRepository.save(guideEntity)).thenReturn(guideEntity);
        when(guidePersistenceMapper.toDomain(guideEntity)).thenReturn(guide);
        //when
        Guide savedGuide = guidePersistenceAdapter.save(guide);
        //then
        assertEquals(guide, savedGuide);
    }

    @Test
    @DisplayName("도움말을 id를 통해 찾는다.")
    void findByIdTest() {
        //given
        GuideEntity guideEntity = GuideEntity.builder()
            .guideId(1L)
            .build();
        Guide guide = Guide.builder()
            .guideId(1L)
            .build();
        when(guideRepository.findById(1L)).thenReturn(java.util.Optional.of(guideEntity));
        when(guidePersistenceMapper.toDomain(guideEntity)).thenReturn(guide);
        //when
        Guide foundGuide = guidePersistenceAdapter.findById(1L).get();
        //then
        assertEquals(guide, foundGuide);
    }

    @Test
    @DisplayName("도움말을 카테고리 id를 통해 찾는다.")
    void findByCategoryIdTest() {
        //given
        GuideEntity guideEntity = GuideEntity.builder()
            .guideId(1L)
            .build();
        Guide guide = Guide.builder()
            .guideId(1L)
            .build();
        when(guideRepository.findByCategory_CategoryId(1L)).thenReturn(java.util.Optional.of(guideEntity));
        when(guidePersistenceMapper.toDomain(guideEntity)).thenReturn(guide);
        //when
        Guide foundGuide = guidePersistenceAdapter.findByCategoryId(1L).get();
        //then
        assertEquals(guide, foundGuide);
    }

    @Test
    @DisplayName("카테고리 id로 도움말이 존재하는지 확인한다.")
    void existsByCategoryIdTest() {
        //given
        when(guideRepository.existsByCategory_CategoryId(1L)).thenReturn(true);
        //when
        Boolean exists = guidePersistenceAdapter.existsByCategoryId(1L);
        //then
        assertEquals(true, exists);
    }

    @Test
    @DisplayName("도움말을 id를 통해 삭제한다.")
    void deleteByIdTest() {
        //given
        Long guideId = 1L;
        //when
        guidePersistenceAdapter.deleteById(guideId);
        //then
        verify(guideRepository).deleteById(guideId);
    }
}
