package com.wrkr.tickety.domains.ticket.application.usecase.guide;

import com.wrkr.tickety.domains.ticket.application.dto.request.GuideCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.service.Guide.GuideCreateService;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.repository.CategoryRepository;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@UseCase
@RequiredArgsConstructor
public class GuideCreateUseCase {
    private final CategoryRepository categoryRepository;
    private final GuideCreateService guideCreateService;

    public ResponseEntity<ApplicationResponse<GuideResponse>> createGuide(GuideCreateRequest guideCreateRequest, String cryptoCategoryId) {

        //todo: CategoryErrorCode 를 사용하도록 수정
        Category category = categoryRepository.findById(PkCrypto.decrypt(cryptoCategoryId))
                .orElseThrow(() -> ApplicationException.from(GuideErrorCode.GuideNotExist));

        GuideResponse guideResponse = guideCreateService.createGuide(guideCreateRequest, category);

        return ResponseEntity.ok(ApplicationResponse.onSuccess(guideResponse));
    }
}
