package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.domain.GuideDomain;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.springframework.stereotype.Component;

@Component
public class GuideMapper {
    public GuideResponse guideToGuideResponse(GuideDomain guideDomain) {
        return GuideResponse.builder()
                .guideId(PkCrypto.encrypt(guideDomain.getGuideId()))
                .content(guideDomain.getContent())
                .build();
    }

    public PkResponse guideIdToPkResponse(GuideDomain guideDomain) {
        return new PkResponse(PkCrypto.encrypt(guideDomain.getGuideId()));
    }
}
