package com.wrkr.tickety.usecase;


import com.wrkr.tickety.domains.ticket.application.mapper.CategoryMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.persistence.adapter.CategoryPersistenceAdapter;
import com.wrkr.tickety.domains.ticket.persistence.entity.CategoryEntity;
import com.wrkr.tickety.domains.ticket.persistence.entity.GuideEntity;
import com.wrkr.tickety.domains.ticket.persistence.entity.TemplateEntity;
import com.wrkr.tickety.domains.ticket.persistence.mapper.CategoryPersistenceMapper;
import com.wrkr.tickety.global.utils.PkCrypto;
import io.micrometer.common.KeyValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CategoryUseCaseTest {

    @Mock
    private CategoryPersistenceAdapter categoryPersistenceAdapter;

    @Mock
    private CategoryPersistenceMapper categoryPersistenceMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryGetService categoryGetService;

    @Mock
    private PkCrypto pkCrypto;

    @BeforeEach
    public void setUp() {

        CategoryEntity firstCategoryEntity = CategoryEntity.builder()
                .categoryId(1L)
                .name("Infra1")
                .seq(1)
                .parent(null)
                .build();

        CategoryEntity secondCategoryEntity = CategoryEntity.builder()
                .categoryId(2L)
                .name("Infra2")
                .seq(2)
                .parent(null)
                .build();

        CategoryEntity updateFirstCategoryEntity = CategoryEntity.builder()
                .categoryId(3L)
                .name("update")
                .seq(1)
                .parent(firstCategoryEntity)
                .build();

        CategoryEntity updateSecondCategoryEntity = CategoryEntity.builder()
                .categoryId(4L)
                .name("update")
                .seq(1)
                .parent(secondCategoryEntity)
                .build();

        List<CategoryEntity> childFirstCategoryEntityList = List.of(updateFirstCategoryEntity);
        List<CategoryEntity> childSecondCategoryEntityList = List.of(updateSecondCategoryEntity);

        CategoryEntity updatedFirstCategoryEntity = CategoryEntity.builder()
                .categoryId(1L)
                .name("Infra1")
                .seq(1)
                .parent(null)
                .build();

        CategoryEntity updatedSecondCategoryEntity = CategoryEntity.builder()
                .categoryId(2L)
                .name("Infra2")
                .seq(2)
                .parent(null)
                .build();

        List<CategoryEntity> categoryEntityList = List.of(updatedFirstCategoryEntity, updatedSecondCategoryEntity);

        TemplateEntity secondCategoryTemplateEntity = TemplateEntity.builder()
                .templateId(2L)
                .content("content")
                .category(updatedSecondCategoryEntity)
                .build();

        GuideEntity firstCategoryGuideEntity = GuideEntity.builder()
                .guideId(1L)
                .content("content")
                .category(updatedFirstCategoryEntity)
                .build();
    }


    @Test
    @DisplayName("관리자가 카테고리를 전체 조회한다.")
    void categoryGetAllTestInUseCase() {
        //FIXME: Implement this test
        // given
//        List<Category> categoryList = categoryEntityList.stream()
//                .map(categoryPersistenceMapper::toDomain)
//                .toList();


        // when

        // then
    }
}
