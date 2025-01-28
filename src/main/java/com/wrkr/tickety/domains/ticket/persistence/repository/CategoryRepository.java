package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByIsDeletedFalse();

    List<CategoryEntity> findByParentCategoryIdAndIsDeletedFalse(Long categoryId);

    Boolean existsByNameAndIsDeletedFalse(String name);

    Boolean existsByNameAndIsDeletedFalseAndCategoryIdNot(String name, Long categoryId);

    Boolean existsByCategoryIdAndIsDeletedFalseAndParentIsNull(Long categoryId);
}
