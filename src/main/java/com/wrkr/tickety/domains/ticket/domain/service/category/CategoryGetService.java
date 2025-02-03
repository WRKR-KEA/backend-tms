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

    public List<Category> byIsDeletedFalse() {
        return categoryPersistenceAdapter.findByIsDeletedFalse();
    }

    public Category getCategory(Long categoryId) {
        return categoryPersistenceAdapter.findById(categoryId)
            .orElseThrow(() -> ApplicationException.from(CategoryErrorCode.CATEGORY_NOT_EXISTS));
    }

    public List<Category> getCategories(List<Long> categoryIds) {
        return categoryPersistenceAdapter.findByIds(categoryIds);
    }

    public List<Category> getChildren(Long categoryId) {
        return categoryPersistenceAdapter.findChildren(categoryId);
    }

    public boolean isCategoryNameExists(Long categoryId, String name) {
        return categoryPersistenceAdapter.isCategoryNameExists(categoryId, name);
    }

    public boolean isCategoryNameExists(String name) {
        return categoryPersistenceAdapter.isCategoryNameExists(name);
    }

    public boolean isCategoryValid(Long categoryId) {
        return categoryPersistenceAdapter.isCategoryValid(categoryId);
    }
}
