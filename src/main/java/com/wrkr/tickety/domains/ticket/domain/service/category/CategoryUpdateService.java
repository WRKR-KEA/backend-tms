package com.wrkr.tickety.domains.ticket.domain.service.category;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.persistence.adapter.CategoryPersistenceAdapter;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryUpdateService {

    private final CategoryPersistenceAdapter categoryPersistenceAdapter;

    public List<Category> updateCategorySequence(List<Category> findCategories, Map<Long, Integer> categoryIdToSeqMap) {
        findCategories.forEach(category -> category.updateSeq(categoryIdToSeqMap.get(category.getCategoryId())));
        return categoryPersistenceAdapter.saveAll(findCategories);
    }

    public Category updateCategoryField(Category findCategory, String name, String abbreviation) {
        findCategory.updateField(name, abbreviation);
        return categoryPersistenceAdapter.save(findCategory);
    }

    public void updateCategoriesSequenceByDeleted(List<Category> categories) {
        categories.forEach(category -> category.updateSeq(category.getSeq() - 1));
        categoryPersistenceAdapter.saveAll(categories);
    }
}
