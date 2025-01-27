package com.wrkr.tickety.domains.ticket.application.usecase.guide;

import com.wrkr.tickety.domains.ticket.application.dto.request.GuideUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.GuideMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideUpdateService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class GuideUpdateUseCase {

    private final GuideUpdateService guideService;
    private final GuideMapper guideMapper;

    public PkResponse modifyGuide(Long cryptoGuideId, GuideUpdateRequest guideUpdateRequest) {
        Guide guide = guideService.updateGuide(cryptoGuideId, guideUpdateRequest);
        return guideMapper.guideIdToPkResponse(guide);
    }
}
