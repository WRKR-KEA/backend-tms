package com.wrkr.tickety.domains.ticket.domain.model;

import com.wrkr.tickety.global.model.BaseTime;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class Category extends BaseTime {

    private Long categoryId;
    private Category parent;
    private String name;
    private Integer seq;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;

    public static Category updateCategory(Category presentCategory, String name, Integer seq) {
        return Category.builder()
                .categoryId(presentCategory.getCategoryId())
                .parent(presentCategory.getParent())
                .name(name == null ? presentCategory.getName() : name)
                .seq(seq == null ? presentCategory.getSeq() : seq)
                .isDeleted(presentCategory.getIsDeleted())
                .deletedAt(presentCategory.getDeletedAt())
                .build();
    }

    public static Category softDeleteCategory(Category presentCategory) {
        return Category.builder()
                .categoryId(presentCategory.getCategoryId())
                .parent(presentCategory.getParent())
                .name(presentCategory.getName())
                .seq(presentCategory.getSeq())
                .isDeleted(true)
                .deletedAt(LocalDateTime.now())
                .build();
    }
}
