package com.wrkr.tickety.domains.ticket.domain.service.category;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.persistence.adapter.CategoryPersistenceAdapter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryGetService {

    private final CategoryPersistenceAdapter categoryPersistenceAdapter;

    public List<Category> byIsDeleted() {
        return categoryPersistenceAdapter.findByIsDeletedFalse();
    }

    public Category getCategory(Long categoryId) {
        return categoryPersistenceAdapter.findById(categoryId);
    }
}
