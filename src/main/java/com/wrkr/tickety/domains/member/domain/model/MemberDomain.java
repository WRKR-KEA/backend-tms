package com.wrkr.tickety.domains.member.domain.model;

import com.wrkr.tickety.domains.member.application.dto.request.MemberUpdateRequest;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.persistence.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberDomain {
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

    public void modifyMemberInfo(MemberUpdateRequest memberUpdateRequest) {
        this.email = memberUpdateRequest.email();
        this.name = memberUpdateRequest.name();
        this.nickname = memberUpdateRequest.nickname();
        this.department = memberUpdateRequest.department();
        this.position = memberUpdateRequest.position();
        this.phone = memberUpdateRequest.phone();
        this.role = memberUpdateRequest.role();
        this.profileImage = memberUpdateRequest.profileImage();
    }

    public void modifyIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
