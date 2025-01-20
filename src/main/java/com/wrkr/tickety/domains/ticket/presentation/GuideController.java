package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.ticket.application.dto.request.GuideCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.GuideUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.GuideUseCase;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Guide Controller")
@RequestMapping("/guide")
public class GuideController {
    private final GuideUseCase guideUseCase;

    @CustomErrorCodes({GuideErrorCode.GuideNotExist})
    @GetMapping("/{categoryId}")
    public ResponseEntity<ApplicationResponse<GuideResponse>> getGuideContent(@PathVariable Long categoryId) {
        return guideUseCase.getGuide(categoryId);
    }

    @PostMapping("/create")
    public ResponseEntity<ApplicationResponse<GuideResponse>> createGuide(@RequestBody GuideCreateRequest guideCreateRequest) {
        return guideUseCase.createGuide(guideCreateRequest);
    }

    @CustomErrorCodes({GuideErrorCode.GuideNotExist})
    @PostMapping("/update/{guideId}")
    public ResponseEntity<ApplicationResponse<GuideResponse>> updateGuide(@PathVariable Long guideId, @RequestBody GuideUpdateRequest guideUpdateRequest) {

        return guideUseCase.updateGuide(guideId, guideUpdateRequest);
    }

    @CustomErrorCodes({GuideErrorCode.GuideNotExist})
    @PostMapping("/delete/{guideId}")
    public ResponseEntity<ApplicationResponse<Void>> deleteGuide(@PathVariable Long guideId) {


        return guideUseCase.deleteGuide(guideId);
    }


}
