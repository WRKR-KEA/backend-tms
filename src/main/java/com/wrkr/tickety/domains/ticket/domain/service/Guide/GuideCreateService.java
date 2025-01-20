package com.wrkr.tickety.domains.ticket.domain.service.Guide;

import com.wrkr.tickety.domains.ticket.application.dto.request.GuideCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.GuideMapper;
import com.wrkr.tickety.domains.ticket.domain.GuideDomain;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.repository.GuideRepository;
import com.wrkr.tickety.global.exception.ApplicationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class GuideCreateService {
    private final GuideRepository guideRepository;
    private final GuideMapper guideMapper;

    public GuideResponse createGuide(GuideCreateRequest guideCreateRequest, Category categoryEntity) {
        Optional<Guide> existingGuide = guideRepository.findByCategory(categoryEntity);

        if (existingGuide.isPresent()) throw ApplicationException.from(GuideErrorCode.GuideAlreadyExist);

        GuideDomain guideDomain = GuideMapper.GuideCreateRequestToDomain(guideCreateRequest);
        Guide guide = Guide.toEntity(guideDomain, categoryEntity);

        guideRepository.save(guide);
        return guideMapper.guideToGuideResponse(guideDomain);
    }
}
