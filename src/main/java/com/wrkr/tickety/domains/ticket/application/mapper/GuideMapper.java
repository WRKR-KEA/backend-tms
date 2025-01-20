package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import org.springframework.stereotype.Component;

@Component
public class GuideMapper {
    /**
     * 도움말 조회 요청시 ENTITY DTO 변환
     */
    public GuideResponse toGuideResponse(Guide guideEntity) {
        if (guideEntity == null) {
            return null;
        }
        return GuideResponse.builder()
                .content(guideEntity.getContent())
                .guideId(guideEntity.getGuideId())
                .build();
    }

    public Guide toGuideEntity(String content, Category category) {
        return Guide.builder()
                .category(category)
                .content(content)
                .build();
    }
}
