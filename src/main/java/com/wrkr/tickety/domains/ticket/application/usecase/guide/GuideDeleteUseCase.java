package com.wrkr.tickety.domains.ticket.application.usecase.guide;

import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideDeleteService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class GuideDeleteUseCase {

    private final GuideDeleteService guideDeleteService;

    public PkResponse deleteGuide(Long guideId) {

        PkResponse pkResponse = guideDeleteService.deleteGuide(guideId);

        return pkResponse;
    }
}
