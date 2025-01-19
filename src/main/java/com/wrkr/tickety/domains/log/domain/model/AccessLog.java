package com.wrkr.tickety.domains.log.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "access_log")
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accessLogId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false, updatable = false)
    private LocalDateTime accessAt;

    @Builder
    public AccessLog(String nickname, String email, String ip) {
        this.nickname = nickname;
        this.email = email;
        this.ip = ip;
    }

    @PrePersist
    protected void onCreate() {
        this.accessAt = LocalDateTime.now();
    }
}
