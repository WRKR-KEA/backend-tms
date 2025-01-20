package com.wrkr.tickety.domains.member.domain.model;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "member")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String phone;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 50)
    private String position;

    @Column(nullable = false)
    private String profileImage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private String agitUrl;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Boolean agitNotification;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Boolean emailNotification;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Boolean serviceNotification;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Boolean isDeleted;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Boolean isTempPassword;

    @Builder
    public Member(
            String nickname, String password, String name, String phone, String email, String position, String profileImage, Role role, String agitUrl
    ) {
        this.nickname = nickname;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.position = position;
        this.profileImage = profileImage;
        this.role = role;
        this.agitUrl = agitUrl;
    }
}
