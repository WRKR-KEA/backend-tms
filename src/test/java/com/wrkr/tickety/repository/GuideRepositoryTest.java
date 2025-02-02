package com.wrkr.tickety.repository;


import com.wrkr.tickety.domains.ticket.persistence.entity.CategoryEntity;
import com.wrkr.tickety.domains.ticket.persistence.entity.GuideEntity;
import com.wrkr.tickety.domains.ticket.persistence.repository.GuideRepository;
import com.wrkr.tickety.global.config.TestConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class GuideRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(GuideRepositoryTest.class);
    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    EntityManager entityManager;

    private GuideEntity guideEntity;

    @Nested
    class findByCategoryTest {

        @BeforeEach
        public void setup() {
            CategoryEntity categoryEntity = CategoryEntity.builder()
                .name("category")
                .isDeleted(false)
                .seq(1)
                .build();
            entityManager.persist(categoryEntity);
            entityManager.flush();

            guideEntity = GuideEntity.builder()
                .category(categoryEntity)
                .content("guide")
                .build();
            entityManager.persist(guideEntity);
            entityManager.flush();
        }

        @Test
        @DisplayName("카테고리 아이디로 가이드를 찾는다.")
        public void findByCategory_CategoryIdTest() {
            //when
            GuideEntity foundGuideEntity = guideRepository.findByCategory_CategoryId(1L).orElseThrow();
            //then
            assert foundGuideEntity.getContent().equals(guideEntity.getContent());
        }

        @Test
        @DisplayName("카테고리id와 일치하는 가이드가 없으면 빈 Optional을 반환한다.")
        public void findByCategory_CategoryId_NotFoundTest() {
            //when
            var foundGuideEntity = guideRepository.findByCategory_CategoryId(100L);
            //then
            assert foundGuideEntity.isEmpty();
        }
    }

    @Nested
    class existsByCategoryTest {

        @Test
        @DisplayName("카테고리 아이디로 가이드가 존재하는지 확인한다. 카테고리가 있을때를 테스트한다.")
        public void existsByCategory_CategoryIdTest() {
            //when
            boolean exists = guideRepository.existsByCategory_CategoryId(1L);
            //then
            assert !exists;
        }

        @Test
        @DisplayName("카테고리 아이디로 가이드가 존재하는지 확인한다. 카테고리가 없을때를 테스트한다.")
        public void existsByCategory_CategoryId_NotFoundTest() {
            //when
            boolean exists = guideRepository.existsByCategory_CategoryId(2L);
            //then
            assert !exists;
        }
    }
}
