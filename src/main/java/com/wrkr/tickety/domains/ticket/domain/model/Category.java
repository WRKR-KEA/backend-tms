package com.wrkr.tickety.domains.ticket.domain.model;

import com.wrkr.tickety.domains.ticket.persistence.entity.CategoryEntity;
import com.wrkr.tickety.global.model.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder
@Table(name = "category")
public class Category extends BaseTime {

    private Long categoryId;
    private Category parent;
    private String name;
    private Integer seq;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private List<Category> children;

    @Builder
    public Category(
            Long categoryId,
            Category parent,
            String name,
            Integer seq,
            Boolean isDeleted,
            LocalDateTime deletedAt,
            List<Category> children
    ) {
        this.categoryId = categoryId;
        this.parent = parent;
        this.name = name;
        this.seq = seq;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.children = children != null ? children : new ArrayList<>();
    }
}
