package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.domain.GuideDomain;
import com.wrkr.tickety.global.utils.PkCrypto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

@Component
public class GuideMapper {
    @Schema(description = "도움말 도메인 -> 응답 변환")
    public GuideResponse guideToGuideResponse(GuideDomain guideDomain) {
        return GuideResponse.builder()
                .guideId(PkCrypto.encrypt(guideDomain.getGuideId()))
                .content(guideDomain.getContent())
                .build();
    }

    @Schema(description = "암호화된 도움말 ID -> 응답 변환")
    public PkResponse guideIdToPkResponse(GuideDomain guideDomain) {
        return new PkResponse(PkCrypto.encrypt(guideDomain.getGuideId()));
    }
}
