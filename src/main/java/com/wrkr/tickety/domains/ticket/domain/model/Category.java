package com.wrkr.tickety.domains.ticket.domain.model;

import com.wrkr.tickety.global.model.BaseTime;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class Category extends BaseTime {

    private Long categoryId;
    private Category parent;
    private String name;
    private Integer seq;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;

    @Builder
    public Category(
        Long categoryId,
        Category parent,
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

    public void updateName(String name) {
        this.name = name;
    }

    public void updateSeq(Integer seq) {
        this.seq = seq;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }


}
