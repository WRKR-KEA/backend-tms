package com.wrkr.tickety.domains.ticket.application.usecase.guide;

import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.domain.service.Guide.GuideGetService;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.ApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@UseCase
@RequiredArgsConstructor
public class GuideGetUseCase {

    private final GuideGetService guideService;

    public ResponseEntity<ApplicationResponse<GuideResponse>> getGuide(String cryptoCategoryId) throws ApplicationException {

        GuideResponse guideResponseByCategory = guideService.getGuideContentByCategory(cryptoCategoryId);

        return ResponseEntity.ok(ApplicationResponse.onSuccess(guideResponseByCategory));

    }
}
