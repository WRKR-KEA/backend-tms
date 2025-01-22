package com.wrkr.tickety.domains.ticket.persistence.adapter;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.entity.CategoryEntity;
import com.wrkr.tickety.domains.ticket.persistence.mapper.CategoryPersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.repository.CategoryRepository;
import com.wrkr.tickety.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryPersistenceAdapter {

    private final CategoryRepository categoryRepository;
    private final CategoryPersistenceMapper categoryPersistenceMapper;

    public Optional<Category> findById(final Long categoryId) {
        final Optional<CategoryEntity> categoryEntity = this.categoryRepository.findById(categoryId);
        return categoryEntity.map(this.categoryPersistenceMapper::toDomain);
    }

    public List<Category> findByIsDeletedFalse() {
        final List<CategoryEntity> categoryEntities = categoryRepository.findByIsDeletedFalse()
                .orElseThrow(() -> ApplicationException.from(CategoryErrorCode.CATEGORY_NOT_EXIST));
        return categoryEntities.stream()
                .map(this.categoryPersistenceMapper::toDomain)
                .toList();
    }
}
