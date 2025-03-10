package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.persistence.entity.GuideEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideRepository extends JpaRepository<GuideEntity, Long> {

    Optional<GuideEntity> findByCategory_CategoryId(Long category_id);

    Boolean existsByCategory_CategoryId(Long categoryId);

    List<GuideEntity> findByCategory_CategoryIdIn(List<Long> categoryIds);
}
