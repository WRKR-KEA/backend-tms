package com.wrkr.tickety.domains.ticket.domain.service.category;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.persistence.adapter.CategoryPersistenceAdapter;
import java.util.List;
import java.util.Optional;

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

    public Optional<Category> getCategory(Long categoryId) {
        return categoryPersistenceAdapter.findById(categoryId);
    }

    public List<Category> getChildren(Long categoryId) {
        return categoryPersistenceAdapter.findChildren(categoryId);
    }
}
