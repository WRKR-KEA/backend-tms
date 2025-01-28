package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.persistence.entity.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {

    Boolean existsByCategory_CategoryId(Long categoryId);

    Optional<TemplateEntity> findByCategory_CategoryId(Long categoryId);
}
