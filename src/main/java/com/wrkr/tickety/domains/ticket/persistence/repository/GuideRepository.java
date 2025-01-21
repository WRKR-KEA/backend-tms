package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuideRepository extends JpaRepository<Guide, Long> {
    Optional<Guide> findByCategory_CategoryId(Long category_id);

    Optional<Guide> findByCategory(Category category);
  
    Boolean existsByCategory_CategoryId(Long categoryId);
}
