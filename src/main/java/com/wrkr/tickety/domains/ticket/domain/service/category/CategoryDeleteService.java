package com.wrkr.tickety.domains.ticket.domain.service.category;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.persistence.adapter.CategoryPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryDeleteService {

    private final CategoryPersistenceAdapter categoryPersistenceAdapter;

    public Category softDeleteCategory(Category category, List<Category> children) { return categoryPersistenceAdapter.softDelete(category, children);}
}
