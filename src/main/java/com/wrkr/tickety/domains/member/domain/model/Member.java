package com.wrkr.tickety.domains.member.domain.model;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.global.model.BaseTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTime {

    private Long memberId;
    private String nickname;
    private String password;
    private String name;
    private String phone;
    private String email;
    private String position;
    private String profileImage;
    private Role role;
    private String agitUrl;
    private Boolean agitNotification;
    private Boolean emailNotification;
    private Boolean serviceNotification;
    private Boolean isDeleted;

    @Builder
    public Member(
            Long memberId,
            String nickname,
            String password,
            String name,
            String phone,
            String email,
            String position,
            String profileImage,
            Role role,
            String agitUrl,
            Boolean agitNotification,
            Boolean emailNotification,
            Boolean serviceNotification,
            Boolean isDeleted
    ) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.position = position;
        this.profileImage = profileImage;
        this.role = role;
        this.agitUrl = agitUrl;
        this.agitNotification = agitNotification;
        this.emailNotification = emailNotification;
        this.serviceNotification = serviceNotification;
        this.isDeleted = isDeleted;
    }
}
