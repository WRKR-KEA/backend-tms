package com.wrkr.tickety.domains.ticket.domain.service;

import com.wrkr.tickety.domains.ticket.persistence.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class TemplateGetService {
    private final TemplateRepository templateRepository;

    public Boolean existsByCategoryId(Long categoryId) {
        return templateRepository.existsByCategory_CategoryId(categoryId);
    }

}
