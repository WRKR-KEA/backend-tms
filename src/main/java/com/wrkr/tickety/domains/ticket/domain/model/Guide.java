package com.wrkr.tickety.domains.ticket.domain.model;

import com.wrkr.tickety.domains.ticket.domain.GuideDomain;
import com.wrkr.tickety.domains.ticket.persistence.entity.CategoryEntity;
import com.wrkr.tickety.global.entity.BaseTimeEntity;
import com.wrkr.tickety.global.model.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;

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
