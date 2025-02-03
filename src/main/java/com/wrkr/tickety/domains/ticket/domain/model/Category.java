package com.wrkr.tickety.domains.ticket.domain.model;

import com.wrkr.tickety.global.model.BaseTime;
import java.time.LocalDateTime;
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
}
