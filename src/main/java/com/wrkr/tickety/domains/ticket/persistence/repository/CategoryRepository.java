package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.persistence.entity.CategoryEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByIsDeletedFalse();

    Boolean existsByNameAndIsDeletedFalse(String name);

    Boolean existsByNameAndIsDeletedFalseAndCategoryIdNot(String name, Long categoryId);

    List<CategoryEntity> findByParentCategoryIdAndIsDeletedFalse(Long categoryId);

    List<CategoryEntity> findByCategoryIdInAndIsDeletedFalse(List<Long> categoryIds);

    Optional<CategoryEntity> findByCategoryIdAndIsDeletedFalseAndParentIsNull(Long categoryId);

    List<CategoryEntity> findByIsDeletedFalseAndParentIsNull();

    List<CategoryEntity> findByParent_CategoryIdIn(List<Long> categoryIds);

    Optional<CategoryEntity> findByCategoryIdAndIsDeletedFalseAndParentIsNotNull(Long categoryId);
}
