package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.application.dto.request.GuideCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.domain.GuideDomain;
import com.wrkr.tickety.global.utils.PkCrypto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

@Component
public class GuideMapper {
    @Schema(description = "도움말 도메인 -> 응답 변환")
    public GuideResponse guideToGuideResponse(GuideDomain guideDomain) {
        if (guideDomain == null) {
            return null;
        }
        return GuideResponse.builder()
                .content(guideDomain.getContent())
                .guideId(PkCrypto.encrypt(guideDomain.getGuideId()))
                .build();
    }

    @Schema(description = "도움말 생성 요청 -> 도메인 변환")
    public static GuideDomain GuideCreateRequestToDomain(GuideCreateRequest guideCreateRequest) {
        if (guideCreateRequest == null) {
            return null;
        }
        return GuideDomain.builder()
                .content(guideCreateRequest.getContent())
                .build();
    }
}
