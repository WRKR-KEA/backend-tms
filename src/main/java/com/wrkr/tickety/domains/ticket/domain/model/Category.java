package com.wrkr.tickety.domains.ticket.domain.model;

import com.wrkr.tickety.global.model.BaseTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
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
}
