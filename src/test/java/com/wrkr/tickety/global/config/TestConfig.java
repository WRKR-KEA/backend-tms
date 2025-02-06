package com.wrkr.tickety.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@TestConfiguration
@EntityScan(basePackages = {
    "com.wrkr.tickety.domains.log.persistence.entity",
    "com.wrkr.tickety.domains.ticket.persistence.entity",
    "com.wrkr.tickety.domains.member.persistence.entity",
    "com.wrkr.tickety.global.entity"
})
@EnableJpaAuditing
public class TestConfig {

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
