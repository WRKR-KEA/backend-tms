package com.wrkr.tickety.domains.ticket.domain.model;

import com.wrkr.tickety.global.model.BaseTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guide extends BaseTime {

    private Long guideId;
    private Category category;
    private String content;

    @Builder
    public Guide(
            Long guideId,
            Category category,
            String content
    ) {
        this.guideId = guideId;
        this.category = category;
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
