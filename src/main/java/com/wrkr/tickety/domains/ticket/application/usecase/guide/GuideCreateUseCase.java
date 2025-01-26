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

@UseCase
@RequiredArgsConstructor
public class GuideCreateUseCase {

    private final GuideCreateService guideCreateService;
    private final CategoryGetService categoryGetService;
    private final GuideMapper guideMapper;

    public PkResponse createGuide(GuideCreateRequest guideCreateRequest, String cryptoCategoryId) {
        //todo 카테고리 도메인이 아직 없어서 카테고리 도메인이 생기면 dto <-> 카테고리 도메인 mapper에서 암호화/복호화 로직을 처리하는 방향으로 수정 예정

        Category category = categoryGetService.getCategory(guideCreateRequest.categoryId())
                .orElseThrow(() -> ApplicationException.from(CategoryErrorCode.CATEGORY_NOT_EXIST));

        Guide guide = Guide.builder()
            .content(guideCreateRequest.content())
            .category(category)
            .build();

        Guide savedGuide = guideCreateService.createGuide(guide);

        return guideMapper.guideIdToPkResponse(savedGuide);
    }
}
