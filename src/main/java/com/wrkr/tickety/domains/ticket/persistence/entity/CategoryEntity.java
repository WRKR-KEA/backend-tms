package com.wrkr.tickety.domains.ticket.persistence.entity;

import com.wrkr.tickety.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "category")
public class CategoryEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CategoryEntity parent;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer seq;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Boolean isDeleted;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    public CategoryEntity(
        Long categoryId,
        CategoryEntity parent,
        String name,
        Integer seq,
        Boolean isDeleted,
        LocalDateTime deletedAt
    ) {
        this.categoryId = categoryId;
        this.parent = parent;
        this.name = name;
        this.seq = seq;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
    }
}
