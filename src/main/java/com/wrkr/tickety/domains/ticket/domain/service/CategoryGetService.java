package com.wrkr.tickety.domains.ticket.domain.service;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.persistence.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryGetService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Optional<Category> getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }
}
