package com.wrkr.tickety.domains.ticket.application.usecase.guide;

import com.wrkr.tickety.domains.ticket.application.dto.request.GuideCreateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.GuideMapper;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.domain.service.category.CategoryGetService;
import com.wrkr.tickety.domains.ticket.domain.service.guide.GuideCreateService;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class GuideCreateUseCase {

    private final GuideCreateService guideCreateService;
    private final CategoryGetService categoryGetService;
    private final GuideMapper guideMapper;

    public PkResponse createGuide(GuideCreateRequest guideCreateRequest, Long categoryId) {

        Category category = categoryGetService.getCategory(categoryId).orElseThrow(
            () -> new ApplicationException(CategoryErrorCode.CATEGORY_NOT_EXISTS)
        );

        Guide guide = Guide.builder()
            .content(guideCreateRequest.content())
            .category(category)
            .build();

        Guide savedGuide = guideCreateService.createGuide(guide);

        return guideMapper.guideIdToPkResponse(savedGuide);
    }
}
