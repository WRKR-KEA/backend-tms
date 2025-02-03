package com.wrkr.tickety.domains.ticket.persistence.adapter;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.persistence.entity.CategoryEntity;
import com.wrkr.tickety.domains.ticket.persistence.mapper.CategoryPersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class CategoryPersistenceAdapter {

    private final CategoryRepository categoryRepository;
    private final CategoryPersistenceMapper categoryPersistenceMapper;

    public Optional<Category> findById(final Long categoryId) {
        final Optional<CategoryEntity> categoryEntity = this.categoryRepository.findByCategoryIdAndIsDeletedFalse(categoryId);
        return categoryEntity.map(this.categoryPersistenceMapper::toDomain);
    }

    public List<Category> findByIsDeletedFalse() {
        final List<CategoryEntity> categoryEntities = categoryRepository.findByIsDeletedFalse();
        return categoryEntities.stream()
            .map(this.categoryPersistenceMapper::toDomain)
            .toList();
    }

    public List<Category> findChildren(Long categoryId) {
        final List<CategoryEntity> categoryEntities = categoryRepository.findByParentCategoryIdAndIsDeletedFalse(categoryId);
        return categoryEntities.stream()
            .map(this.categoryPersistenceMapper::toDomain)
            .toList();
    }

    public Category save(final Category category) {
        final CategoryEntity categoryEntity = this.categoryPersistenceMapper.toEntity(category);
        final CategoryEntity savedCategoryEntity = this.categoryRepository.save(categoryEntity);
        return this.categoryPersistenceMapper.toDomain(savedCategoryEntity);
    }

    public List<Category> saveAll(List<Category> requestCategories) {
        final List<CategoryEntity> categoryEntities = requestCategories.stream()
            .map(this.categoryPersistenceMapper::toEntity)
            .toList();

        final List<CategoryEntity> savedCategoryEntities = this.categoryRepository.saveAll(categoryEntities);
        return savedCategoryEntities.stream()
            .map(this.categoryPersistenceMapper::toDomain)
            .toList();
    }

    public List<Category> findByIds(List<Long> categoryIds) {
        final List<CategoryEntity> categoryEntities = this.categoryRepository.findByCategoryIdInAndIsDeletedFalse(categoryIds);
        return categoryEntities.stream()
            .map(this.categoryPersistenceMapper::toDomain)
            .toList();
    }

    public boolean isCategoryNameExists(Long categoryId, String name) {
        return this.categoryRepository.existsByNameAndIsDeletedFalseAndCategoryIdNot(name, categoryId);
    }

    public boolean isCategoryNameExists(String name) {
        return this.categoryRepository.existsByNameAndIsDeletedFalse(name);
    }
}
