package com.wrkr.tickety.domains.member.domain.model;

import com.wrkr.tickety.domains.member.application.dto.request.MemberInfoUpdateRequest;
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
    private String department;
    private String position;
    private String profileImage;
    private Role role;
    private String agitUrl;
    private Boolean agitNotification;
    private Boolean emailNotification;
    private Boolean serviceNotification;
    private Boolean isDeleted;
    private Boolean isTempPassword;

    @Builder
    public Member(
        Long memberId,
        String nickname,
        String password,
        String name,
        String phone,
        String email,
        String department,
        String position,
        String profileImage,
        Role role,
        String agitUrl,
        Boolean agitNotification,
        Boolean emailNotification,
        Boolean serviceNotification,
        Boolean isDeleted,
        Boolean isTempPassword
    ) {
        this.memberId = memberId;
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

    // TODO: 필드 단위로 파라미터 받기
    public void modifyMemberInfo(
        MemberInfoUpdateRequest memberInfoUpdateRequest
    ) {
        this.email = memberInfoUpdateRequest.email();
        this.name = memberInfoUpdateRequest.name();
        this.nickname = memberInfoUpdateRequest.nickname();
        this.department = memberInfoUpdateRequest.department();
        this.position = memberInfoUpdateRequest.position();
        this.phone = memberInfoUpdateRequest.phone();
        this.role = memberInfoUpdateRequest.role();
        this.agitUrl = memberInfoUpdateRequest.agitUrl();
    }

    public void modifyIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void modifyMyPageInfo(String position, String phone) {
        this.position = position;
        this.phone = phone;
    }

    public void modifyPassword(String password, Boolean isTempPassword) {
        this.password = password;
        this.isTempPassword = isTempPassword;
    }

    public void modifyProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Boolean isDeleted() {
        return isDeleted;
    }

    public boolean isManager() {
        return role == Role.MANAGER;
    }
}
