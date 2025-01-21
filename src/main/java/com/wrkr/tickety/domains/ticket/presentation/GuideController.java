package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.ticket.application.dto.request.GuideCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.GuideUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideDeleteUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideUpdateUseCase;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Guide Controller")
@RequestMapping("/guide")
public class GuideController {
    private final GuideCreateUseCase guideCreateUseCase;
    private final GuideUpdateUseCase guideUpdateUseCase;
    private final GuideGetUseCase guideGetUseCase;
    private final GuideDeleteUseCase guideDeleteUseCase;


    /**
     * @see com.wrkr.tickety.global.utils.PkCrypto
     * */
    @Operation(summary = "도움말 조회")
    @Parameter(name = "cryptoCategoryId",
            description = "암호화된 카테고리 키",
            example = "Gbdsnz3dU0kwFxKpavlkog==",
            required = true,
            in = ParameterIn.PATH)
    @CustomErrorCodes({GuideErrorCode.GuideNotExist})
    @GetMapping("/{cryptoCategoryId}")
    public ResponseEntity<ApplicationResponse<GuideResponse>> getGuideContent(@PathVariable String cryptoCategoryId) {
        return guideGetUseCase.getGuide(cryptoCategoryId);
    }

    /**
     * @see com.wrkr.tickety.global.utils.PkCrypto
     * */
    @Operation(summary = "도움말 생성")
    @Parameter(name = "cryptoCategoryId",
            description = "암호화된 카테고리 키",
            example = "Gbdsnz3dU0kwFxKpavlkog==",
            required = true,
            in = ParameterIn.PATH)
    @PostMapping("/{cryptoCategoryId}")
    public ResponseEntity<ApplicationResponse<GuideResponse>> createGuide(@RequestBody GuideCreateRequest guideCreateRequest, @PathVariable String cryptoCategoryId) {
        return guideCreateUseCase.createGuide(guideCreateRequest, cryptoCategoryId);
    }

    /**
     * @see com.wrkr.tickety.global.utils.PkCrypto
     * */
    @Operation(summary = "도움말 수정")
    @Parameter(name = "cryptoCategoryId",
            description = "암호화된 도움말 키",
            example = "Gbdsnz3dU0kwFxKpavlkog==",
            required = true,
            in = ParameterIn.PATH)
    @CustomErrorCodes({GuideErrorCode.GuideNotExist})
    @PatchMapping("/{cryptoGuideId}")
    public ResponseEntity<ApplicationResponse<GuideResponse>> updateGuide(@PathVariable String cryptoGuideId, @RequestBody GuideUpdateRequest guideUpdateRequest) {

        return guideUpdateUseCase.modifyGuide(cryptoGuideId, guideUpdateRequest);
    }

    /**
     * @see com.wrkr.tickety.global.utils.PkCrypto
     * */
    @Operation(summary = "도움말 삭제")
    @Parameter(name = "cryptoCategoryId",
            description = "암호화된 도움말 키",
            example = "Gbdsnz3dU0kwFxKpavlkog==",
            required = true,
            in = ParameterIn.PATH)
    @CustomErrorCodes({GuideErrorCode.GuideNotExist})
    @DeleteMapping("/{cryptoGuideId}")
    public ResponseEntity<ApplicationResponse<Void>> deleteGuide(@PathVariable String cryptoGuideId) {


        return guideDeleteUseCase.deleteGuide(cryptoGuideId);
    }


}
