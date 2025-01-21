package com.wrkr.tickety.domains.ticket.application.usecase.guide;

import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.GuideMapper;
import com.wrkr.tickety.domains.ticket.domain.GuideDomain;
import com.wrkr.tickety.domains.ticket.domain.service.Guide.GuideGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class GuideGetUseCase {

    private final GuideGetService guideGetService;
    private final GuideMapper guideMapper;

    public GuideResponse getGuide(String cryptoCategoryId) {

        GuideDomain guideDomain = guideGetService.getGuideContentByCategory(cryptoCategoryId);
        GuideResponse guideResponse = guideMapper.guideToGuideResponse(guideDomain);

        return guideResponse;

    }
}
