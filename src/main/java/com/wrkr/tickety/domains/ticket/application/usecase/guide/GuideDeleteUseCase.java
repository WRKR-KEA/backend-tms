package com.wrkr.tickety.domains.ticket.application.usecase.guide;

import com.wrkr.tickety.domains.ticket.domain.service.Guide.GuideDeleteService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.response.ApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@UseCase
@RequiredArgsConstructor
public class GuideDeleteUseCase {

    private final GuideDeleteService guideService;

    public ResponseEntity<ApplicationResponse<Void>> deleteGuide(String cryptoGuideId) {
        guideService.deleteGuide(cryptoGuideId);

        return ResponseEntity.ok(ApplicationResponse.onSuccess());
    }
}
