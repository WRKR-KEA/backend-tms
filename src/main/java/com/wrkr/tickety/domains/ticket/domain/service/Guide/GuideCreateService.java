package com.wrkr.tickety.domains.ticket.domain.service.Guide;

import com.wrkr.tickety.domains.ticket.domain.GuideDomain;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.repository.GuideRepository;
import com.wrkr.tickety.global.exception.ApplicationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GuideCreateService {
    private final GuideRepository guideRepository;

    public GuideDomain createGuide(GuideDomain guideDomain, Category category) {

        boolean isGuideExists = guideRepository.existsByCategory_CategoryId(guideDomain.getCategoryId());
        if (isGuideExists) throw ApplicationException.from(GuideErrorCode.GUIDE_ALREADY_EXIST);

        Guide guide = Guide.toEntity(guideDomain, category);
        Guide savedGuide = guideRepository.save(guide);
        return GuideDomain.toDomain(savedGuide);
    }
}
