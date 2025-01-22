package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.persistence.entity.GuideEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuideRepository extends JpaRepository<GuideEntity, Long> {

    Optional<GuideEntity> findByCategory_CategoryId(Long category_id);
    Boolean existsByCategory_CategoryId(Long categoryId);
}
