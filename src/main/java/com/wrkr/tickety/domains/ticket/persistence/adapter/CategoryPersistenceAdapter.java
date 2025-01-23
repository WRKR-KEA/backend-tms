package com.wrkr.tickety.domains.ticket.persistence.adapter;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.entity.CategoryEntity;
import com.wrkr.tickety.domains.ticket.persistence.mapper.CategoryPersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.repository.CategoryRepository;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryPersistenceAdapter {

    private final CategoryRepository categoryRepository;
    private final CategoryPersistenceMapper categoryPersistenceMapper;

    public Category findById(final Long categoryId) {
        final CategoryEntity categoryEntity = this.categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ApplicationException(CategoryErrorCode.CATEGORY_NOT_FOUND));
        return this.categoryPersistenceMapper.toDomain(categoryEntity);
    }

    public List<Category> findByIsDeletedFalse() {
        final List<CategoryEntity> categoryEntities = categoryRepository.findByIsDeletedFalse();
        return categoryEntities.stream()
            .map(this.categoryPersistenceMapper::toDomain)
            .toList();
    }
}
