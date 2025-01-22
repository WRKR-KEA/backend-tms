package com.wrkr.tickety.domains.member.persistence.mapper;

import com.wrkr.tickety.domains.member.domain.model.MemberDomain;
import com.wrkr.tickety.domains.member.persistence.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberPersistenceMapper {

    public static Member toEntity(MemberDomain memberDomain) {
        return Member.builder()
                .nickname(memberDomain.getNickname())
                .password(memberDomain.getPassword())
                .name(memberDomain.getName())
                .phone(memberDomain.getPhone())
                .email(memberDomain.getEmail())
                .department(memberDomain.getDepartment())
                .position(memberDomain.getPosition())
                .profileImage(memberDomain.getProfileImage())
                .role(memberDomain.getRole())
                .agitUrl(memberDomain.getAgitUrl())
                .agitNotification(memberDomain.getAgitNotification())
                .emailNotification(memberDomain.getEmailNotification())
                .serviceNotification(memberDomain.getServiceNotification())
                .isDeleted(memberDomain.getIsDeleted())
                .isTempPassword(memberDomain.getIsTempPassword())
                .build();
    }

    public static MemberDomain toDomain(Member member) {
        return MemberDomain.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .password(member.getPassword())
                .name(member.getName())
                .phone(member.getPhone())
                .email(member.getEmail())
                .department(member.getDepartment())
                .position(member.getPosition())
                .profileImage(member.getProfileImage())
                .role(member.getRole())
                .agitUrl(member.getAgitUrl())
                .agitNotification(member.getAgitNotification())
                .emailNotification(member.getEmailNotification())
                .serviceNotification(member.getServiceNotification())
                .isDeleted(member.getIsDeleted())
                .isTempPassword(member.getIsTempPassword())
                .build();
    }
}
