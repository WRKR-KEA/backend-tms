package com.wrkr.tickety.domains.log.persistence.entity;

import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "access_log")
public class AccessLogEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accessLogId;

    @Column
    private String nickname;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType action;

    @Column(nullable = false, updatable = false)
    private LocalDateTime accessAt;

    @Column(nullable = false)
    private Boolean isSuccess;

    @Builder
    public AccessLogEntity(Long accessLogId, String nickname, Role role, String ip, ActionType action, LocalDateTime accessAt, Boolean isSuccess) {
        this.accessLogId = accessLogId;
        this.nickname = nickname;
        this.role = role;
        this.ip = ip;
        this.action = action;
        this.accessAt = accessAt;
        this.isSuccess = isSuccess;
    }

    @PrePersist
    protected void onCreate() {
        this.accessAt = LocalDateTime.now();
    }
}
