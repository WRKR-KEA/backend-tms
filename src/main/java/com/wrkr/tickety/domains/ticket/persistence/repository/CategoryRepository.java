package com.wrkr.tickety.domains.ticket.persistence.repository;

import com.wrkr.tickety.domains.ticket.persistence.entity.CategoryEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long>, CategoryQueryDslRepository {

    List<CategoryEntity> findByIsDeletedFalseAndParentIsNullAndSeqIsGreaterThan(Integer sequence);

    Boolean existsByNameAndIsDeletedFalse(String name);

    Boolean existsByNameAndIsDeletedFalseAndCategoryIdNot(String name, Long categoryId);

    List<CategoryEntity> findByParentCategoryIdAndIsDeletedFalse(Long categoryId);

    List<CategoryEntity> findByCategoryIdInAndIsDeletedFalse(List<Long> categoryIds);

    Optional<CategoryEntity> findByCategoryIdAndIsDeletedFalseAndParentIsNull(Long categoryId);

    List<CategoryEntity> findByIsDeletedFalseAndParentIsNull();

    List<CategoryEntity> findByParent_CategoryIdIn(List<Long> categoryIds);

    Optional<CategoryEntity> findByCategoryIdAndIsDeletedFalseAndParentIsNotNull(Long categoryId);

    Boolean existsByAbbreviationAndIsDeletedFalse(String abbreviation);

    Boolean existsByAbbreviationAndIsDeletedFalseAndCategoryIdNot(String abbreviation, Long categoryId);

    Boolean existsBySeqAndIsDeletedFalse(Integer seq);

    boolean existsByCategoryIdAndIsDeletedFalseAndParentIsNull(Long categoryId);
}
