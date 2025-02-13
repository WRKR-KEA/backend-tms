package com.wrkr.tickety.domains.ticket.domain.service.category;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.adapter.CategoryPersistenceAdapter;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryGetService {

    private final CategoryPersistenceAdapter categoryPersistenceAdapter;

    public Category getParentCategory(Long categoryId) {
        return categoryPersistenceAdapter.findById(categoryId)
            .orElseThrow(() -> ApplicationException.from(CategoryErrorCode.CATEGORY_NOT_EXISTS));
    }

    public Category getChildrenCategory(Long categoryId) {
        return categoryPersistenceAdapter.findChildrenCategoryById(categoryId)
            .orElseThrow(() -> ApplicationException.from(CategoryErrorCode.CATEGORY_NOT_EXISTS));
    }

    public List<Category> getCategories(List<Long> categoryIds) {
        return categoryPersistenceAdapter.findByIds(categoryIds);
    }

    public List<Category> getChildren(Long categoryId) {
        return categoryPersistenceAdapter.findChildren(categoryId);
    }

    public List<Category> getChildrenByCategoryIds(List<Long> categoryIds) {
        return categoryPersistenceAdapter.getChildrenByCategoryIds(categoryIds);
    }

    public boolean isCategoryNameExistsNotMe(Long categoryId, String name) {
        return categoryPersistenceAdapter.isCategoryNameExistsNotMe(categoryId, name);
    }

    public boolean isCategoryNameExists(String name) {
        return categoryPersistenceAdapter.isCategoryNameExists(name);
    }

    public List<Category> findParents() {
        return categoryPersistenceAdapter.findParents();
    }

    public boolean isCategoryAbbreviationExists(String abbreviation) {
        return categoryPersistenceAdapter.isCategoryAbbreviationExists(abbreviation);
    }

    public boolean isCategoryAbbreviationExistsNotMe(Long categoryId, String abbreviation) {
        return categoryPersistenceAdapter.isCategoryAbbreviationExistsNotMe(categoryId, abbreviation);
    }

    public List<Category> findLowerSequenceCategories(Integer sequence) {
        return categoryPersistenceAdapter.findLowerSequenceCategories(sequence);
    }

    public boolean isCategorySequenceExists(Integer seq) {
        return categoryPersistenceAdapter.isCategorySequenceExists(seq);
    }
}
