package com.wrkr.tickety.domains.ticket.domain.service.category;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.persistence.adapter.CategoryPersistenceAdapter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryDeleteService {

    private final CategoryPersistenceAdapter categoryPersistenceAdapter;

    public Category softDeleteCategory(Category category) {
        category.softDelete();
        return categoryPersistenceAdapter.save(category);
    }

    public void softDeleteCategories(List<Category> children) {
        children.forEach(Category::softDelete);
        categoryPersistenceAdapter.saveAll(children);
    }
}
