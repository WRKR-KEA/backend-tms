package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.persistence.entity.TemplateEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {

    Boolean existsByCategory_CategoryId(Long categoryId);

    List<Long> findByCategory_CategoryIdIn(List<Long> categoryIds);

    Optional<TemplateEntity> findByCategory_CategoryId(Long categoryId);
}
