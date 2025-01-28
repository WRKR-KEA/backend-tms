package com.wrkr.tickety.domains.ticket.application.usecase.guide;

import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.GuideMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class GuideGetUseCase {

    private final GuideGetService guideGetService;
    private final GuideMapper guideMapper;

    public GuideResponse getGuide(Long categoryId) {

        Guide guide = guideGetService.getGuideContentByCategory(categoryId);
        GuideResponse guideResponse = guideMapper.guideToGuideResponse(guide);

        return guideResponse;

    }
}
