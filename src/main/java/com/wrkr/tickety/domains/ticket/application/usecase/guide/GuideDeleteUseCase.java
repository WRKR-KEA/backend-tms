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
    private final PkCrypto pkCrypto;

    public PkResponse deleteGuide(String cryptoGuideId) {
        //todo 삭제 확인 로직 추가?
        PkResponse pkResponse = guideDeleteService.deleteGuide(cryptoGuideId);

        return pkResponse;
    }
}
