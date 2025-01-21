package com.wrkr.tickety.domains.ticket.domain.model;

import com.wrkr.tickety.domains.ticket.domain.GuideDomain;
import com.wrkr.tickety.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "guide")
public class Guide extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guideId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder
    public Guide(Category category, String content) {
        this.category = category;
        this.content = content;
    }

    public static Guide toEntity(GuideDomain guideDomain) {
        return Guide.builder()
                .content(guideDomain.getContent())
                .build();
    }

    public static Guide toEntity(GuideDomain guideDomain, Category category) {
        return Guide.builder()
                .content(guideDomain.getContent())
                .category(category)
                .build();
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
