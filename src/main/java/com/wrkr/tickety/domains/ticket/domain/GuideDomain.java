package com.wrkr.tickety.domains.ticket.domain;


import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GuideDomain {
    private Long guideId;
    private String content;
    private Long categoryId;

    public static GuideDomain toDomain(Guide guide) {
        return GuideDomain.builder()
                .guideId(guide.getGuideId())
                .content(guide.getContent())
                .categoryId(guide.getCategory().getCategoryId())
                .build();
    }

    public void updateContent(String content) {
        this.content = content;
    }
}