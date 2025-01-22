package com.wrkr.tickety.domains.member.persistence.entity;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.MemberDomain;
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
    private String department;

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
            String nickname, String password, String name, String phone, String email, String department, String position, String profileImage, Role role, String agitUrl,
            Boolean agitNotification, Boolean emailNotification, Boolean serviceNotification, Boolean isDeleted, Boolean isTempPassword
    ) {
        this.nickname = nickname;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.department = department;
        this.position = position;
        this.profileImage = profileImage;
        this.role = role;
        this.agitUrl = agitUrl;
        this.agitNotification = agitNotification;
        this.emailNotification = emailNotification;
        this.serviceNotification = serviceNotification;
        this.isDeleted = isDeleted;
        this.isTempPassword = isTempPassword;
    }

    public void modifyMemberInfo(MemberDomain memberDomain) {
        this.email = memberDomain.getEmail();
        this.name = memberDomain.getName();
        this.nickname = memberDomain.getNickname();
        this.department = memberDomain.getDepartment();
        this.position = memberDomain.getPosition();
        this.phone = memberDomain.getPhone();
        this.role = memberDomain.getRole();
        this.profileImage = memberDomain.getProfileImage();
    }
}
