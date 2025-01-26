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
        if(categoryEntity.isEmpty()) {
            throw ApplicationException.from(CategoryErrorCode.CATEGORY_NOT_EXIST);
        }
        return categoryEntity.map(this.categoryPersistenceMapper::toDomain);
    }

    public List<Category> findByIsDeletedFalse() {
        final List<CategoryEntity> categoryEntities = categoryRepository.findByIsDeletedFalse();
        return categoryEntities.stream()
                .map(this.categoryPersistenceMapper::toDomain)
                .toList();
    }

    public Category save(final Category category) {
        if(category.getName().isEmpty() || category.getSeq() == null) {
            throw ApplicationException.from(CategoryErrorCode.CATEGORY_CANNOT_NULL);
        }

        if(categoryRepository.existsByName(category.getName())) {
            throw ApplicationException.from(CategoryErrorCode.CATEGORY_ALREADY_EXIST);
        }

        final CategoryEntity categoryEntity = this.categoryPersistenceMapper.toEntity(category);
        final CategoryEntity savedCategoryEntity = this.categoryRepository.save(categoryEntity);
        return this.categoryPersistenceMapper.toDomain(savedCategoryEntity);
    }

    public void saveAll(List<Category> children) {
        final List<CategoryEntity> childrenEntities = children.stream()
                .map(this.categoryPersistenceMapper::toEntity)
                .toList();
        this.categoryRepository.saveAll(childrenEntities);

    }
}
