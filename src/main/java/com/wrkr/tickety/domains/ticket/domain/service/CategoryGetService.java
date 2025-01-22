package com.wrkr.tickety.domains.ticket.domain.service;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.persistence.adapter.CategoryPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryGetService {

    private final CategoryPersistenceAdapter categoryPersistenceAdapter;

    public List<Category> byIsDeleted() {
        return categoryPersistenceAdapter.findByIsDeletedFalse();
    }

    public Optional<Category> getCategory(Long categoryId) {
        return categoryPersistenceAdapter.findById(categoryId);
    }
}
