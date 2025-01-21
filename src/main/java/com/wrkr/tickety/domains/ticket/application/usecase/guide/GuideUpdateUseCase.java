package com.wrkr.tickety.domains.ticket.application.usecase.guide;

import com.wrkr.tickety.domains.ticket.application.dto.request.GuideUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.domain.service.Guide.GuideUpdateService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.response.ApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@UseCase
@RequiredArgsConstructor
public class GuideUpdateUseCase {
    private final GuideUpdateService guideService;

    public ResponseEntity<ApplicationResponse<GuideResponse>> modifyGuide(String cryptoGuideId, GuideUpdateRequest guideUpdateRequest) {

        GuideResponse guideResponse = guideService.modifyGuide(cryptoGuideId, guideUpdateRequest);

        return ResponseEntity.ok(ApplicationResponse.onSuccess(guideResponse));
    }
}
