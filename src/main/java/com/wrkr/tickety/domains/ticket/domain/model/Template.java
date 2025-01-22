package com.wrkr.tickety.domains.ticket.domain.model;

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
public class Template extends BaseTime {

    private Long templateId;
    private Category category;
    private String content;

    @Builder
    public Template(
            Long templateId,
            Category category,
            String content
    ) {
        this.templateId = templateId;
        this.category = category;
        this.content = content;
    }
}
