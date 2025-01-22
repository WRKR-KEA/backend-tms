package com.wrkr.tickety.domains.ticket.persistence.entity;

import com.wrkr.tickety.domains.ticket.domain.GuideDomain;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "guide")
public class GuideEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guideId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder
    public GuideEntity(
            Long guideId,
            CategoryEntity category,
            String content
    ) {
        this.guideId = guideId;
        this.category = category;
        this.content = content;
    }
}

