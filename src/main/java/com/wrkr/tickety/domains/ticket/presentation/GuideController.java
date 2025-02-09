package com.wrkr.tickety.domains.ticket.presentation;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.ticket.application.dto.request.GuideCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.request.GuideUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideCreateUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideDeleteUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideGetUseCase;
import com.wrkr.tickety.domains.ticket.application.usecase.guide.GuideUpdateUseCase;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import com.wrkr.tickety.global.annotation.swagger.CustomErrorCodes;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.utils.PkCrypto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "Guide Controller")
public class GuideController {

    private final GuideCreateUseCase guideCreateUseCase;
    private final GuideUpdateUseCase guideUpdateUseCase;
    private final GuideGetUseCase guideGetUseCase;
    private final GuideDeleteUseCase guideDeleteUseCase;

    @Operation(summary = "도움말 조회")
    @Parameter(name = "cryptoCategoryId", description = "암호화된 카테고리 키", example = "Gbdsnz3dU0kwFxKpavlkog==", required = true, in = ParameterIn.PATH)
    @CustomErrorCodes({GuideErrorCode.GUIDE_NOT_EXIST})
    @GetMapping("/api/user/guide/{cryptoCategoryId}")
    public ApplicationResponse<GuideResponse> getGuideContent(@AuthenticationPrincipal Member member, @PathVariable String cryptoCategoryId) {
        GuideResponse guideResponse = guideGetUseCase.getGuide(PkCrypto.decrypt(cryptoCategoryId));
        return ApplicationResponse.onSuccess(guideResponse);
    }

    @Operation(summary = "도움말 생성")
    @Parameter(name = "cryptoCategoryId", description = "암호화된 카테고리 키", example = "Gbdsnz3dU0kwFxKpavlkog==", required = true, in = ParameterIn.PATH)
    @Parameter(name = "attachments", description = "첨부파일")
    @PostMapping(value = "/api/admin/guide/{cryptoCategoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Parameter(
        name = "guideCreateRequest",
        description = "도움말 생성 요청 데이터",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = GuideCreateRequest.class)
        )
    )
    public ApplicationResponse<PkResponse> createGuide(
        @RequestPart("guideCreateRequest") GuideCreateRequest guideCreateRequest,
        @PathVariable String cryptoCategoryId,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        PkResponse pkResponse = guideCreateUseCase.createGuide(guideCreateRequest, PkCrypto.decrypt(cryptoCategoryId), attachments);

        return ApplicationResponse.onSuccess(pkResponse);
    }

    @Operation(summary = "도움말 수정")
    @Parameter(name = "cryptoCategoryId", description = "암호화된 도움말 키", example = "Gbdsnz3dU0kwFxKpavlkog==", required = true, in = ParameterIn.PATH)
    @CustomErrorCodes({GuideErrorCode.GUIDE_NOT_EXIST})
    @Parameter(
        name = "guideCreateRequest",
        description = "도움말 수정 요청 데이터",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = GuideCreateRequest.class)
        )
    )
    @Parameter(name = "attachments", description = "첨부파일")
    @PatchMapping(value = "/api/admin/guide/{cryptoGuideId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApplicationResponse<PkResponse> updateGuide(
        @AuthenticationPrincipal Member member,
        @PathVariable String cryptoGuideId,
        @RequestPart GuideUpdateRequest guideUpdateRequest,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        PkResponse pkResponse = guideUpdateUseCase.modifyGuide(PkCrypto.decrypt(cryptoGuideId), guideUpdateRequest, attachments);
        return ApplicationResponse.onSuccess(pkResponse);
    }

    @Operation(summary = "도움말 삭제")
    @Parameter(name = "cryptoCategoryId", description = "암호화된 도움말 키", example = "Gbdsnz3dU0kwFxKpavlkog==", required = true, in = ParameterIn.PATH)
    @CustomErrorCodes({GuideErrorCode.GUIDE_NOT_EXIST})
    @DeleteMapping("/api/admin/guide/{cryptoGuideId}")
    public ApplicationResponse<PkResponse> deleteGuide(@AuthenticationPrincipal Member member, @PathVariable String cryptoGuideId) {
        return ApplicationResponse.onSuccess(guideDeleteUseCase.deleteGuide(PkCrypto.decrypt(cryptoGuideId)));
    }
}
