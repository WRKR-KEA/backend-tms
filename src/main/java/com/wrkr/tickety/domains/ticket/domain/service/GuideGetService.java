package com.wrkr.tickety.domains.ticket.domain.service;

import com.wrkr.tickety.domains.ticket.persistence.repository.GuideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GuideGetService {
    private final GuideRepository guideRepository;

    public Boolean existsByCategoryId(Long categoryId) {
        return guideRepository.existsByCategory_CategoryId(categoryId);
    }

}
