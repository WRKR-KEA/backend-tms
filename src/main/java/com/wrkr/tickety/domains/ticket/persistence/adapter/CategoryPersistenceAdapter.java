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
        final Optional<CategoryEntity> categoryEntity = this.categoryRepository.findByCategoryIdAndIsDeletedFalseAndParentIsNull(categoryId);
        return categoryEntity.map(this.categoryPersistenceMapper::toDomain);
    }

    public Optional<Category> findChildrenCategoryById(final Long categoryId) {
        final Optional<CategoryEntity> categoryEntity = this.categoryRepository.findByCategoryIdAndIsDeletedFalseAndParentIsNotNull(categoryId);
        return categoryEntity.map(this.categoryPersistenceMapper::toDomain);
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

    public boolean isCategoryNameExistsNotMe(Long categoryId, String name) {
        return this.categoryRepository.existsByNameAndIsDeletedFalseAndCategoryIdNot(name, categoryId);
    }

    public boolean isCategoryNameExists(String name) {
        return this.categoryRepository.existsByNameAndIsDeletedFalse(name);
    }

    public List<Category> findParents() {
        final List<CategoryEntity> categoryEntities = this.categoryRepository.findByIsDeletedFalseAndParentIsNull();
        return categoryEntities.stream()
            .map(this.categoryPersistenceMapper::toDomain)
            .toList();
    }

    public List<Category> getChildrenByCategoryIds(List<Long> categoryIds) {
        final List<CategoryEntity> categoryEntities = this.categoryRepository.findByParent_CategoryIdIn(categoryIds);
        return categoryEntities.stream()
            .map(this.categoryPersistenceMapper::toDomain)
            .toList();
    }

    public boolean isCategoryAbbreviationExists(String abbreviation) {
        return this.categoryRepository.existsByAbbreviationAndIsDeletedFalse(abbreviation);
    }

    public boolean isCategoryAbbreviationExistsNotMe(Long categoryId, String abbreviation) {
        return this.categoryRepository.existsByAbbreviationAndIsDeletedFalseAndCategoryIdNot(abbreviation, categoryId);
    }

    public List<Category> findLowerSequenceCategories(Integer sequence) {
        final List<CategoryEntity> categoryEntities = this.categoryRepository.findByIsDeletedFalseAndParentIsNullAndSeqIsGreaterThan(sequence);
        return categoryEntities.stream()
            .map(this.categoryPersistenceMapper::toDomain)
            .toList();
    }

    public boolean isCategorySequenceExists(Integer seq) {
        return this.categoryRepository.existsBySeqAndIsDeletedFalse(seq);
    }
}
