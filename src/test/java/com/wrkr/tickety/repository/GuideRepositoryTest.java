package com.wrkr.tickety.repository;


import com.wrkr.tickety.domains.ticket.persistence.entity.CategoryEntity;
import com.wrkr.tickety.domains.ticket.persistence.entity.GuideEntity;
import com.wrkr.tickety.domains.ticket.persistence.repository.GuideRepository;
import com.wrkr.tickety.global.config.TestConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class GuideRepositoryTest {

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    public void findByCategory_CategoryIdTest() {
        //given
        CategoryEntity categoryEntity = CategoryEntity.builder()
            .name("category")
            .isDeleted(false)
            .seq(1)
            .build();
        entityManager.persist(categoryEntity);
        entityManager.flush();

        GuideEntity guideEntity = GuideEntity.builder()
            .category(categoryEntity)
            .content("guide")
            .build();
        entityManager.persist(guideEntity);
        entityManager.flush();
        //when
        GuideEntity foundGuideEntity = guideRepository.findByCategory_CategoryId(1L).orElseThrow();
        //then
        assert foundGuideEntity.getContent().equals(guideEntity.getContent());
    }

    @Test
    public void existsByCategory_CategoryIdTest() {
        //given
        CategoryEntity categoryEntity = CategoryEntity.builder()
            .name("category")
            .isDeleted(false)
            .seq(1)
            .build();
        entityManager.persist(categoryEntity);
        entityManager.flush();

        GuideEntity guideEntity = GuideEntity.builder()
            .category(categoryEntity)
            .content("guide")
            .build();
        entityManager.persist(guideEntity);
        entityManager.flush();
        //when
        boolean exists = guideRepository.existsByCategory_CategoryId(1L);
        //then
        assert exists;
    }
}
