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

    /**
     * isDeleted 값이 false인 카테고리 목록을 조회한다.
     */
    public List<Category> byIsDeleted() {

        return categoryPersistenceAdapter.findByIsDeletedFalse();
    }

    public Category getCategory(Long categoryId) {
    }
  
    public Optional<Category> getCategory(Long categoryId) {
        return categoryPersistenceAdapter.findById(categoryId);
    }

}
