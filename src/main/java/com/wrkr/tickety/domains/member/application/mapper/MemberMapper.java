package com.wrkr.tickety.domains.member.application.mapper;

import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateReqDTO;
import com.wrkr.tickety.domains.member.application.dto.response.MemberCreateResDTO;
import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResDTO;
import com.wrkr.tickety.domains.member.domain.model.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public static Member toMember(MemberCreateReqDTO reqDTO, String encryptedPassword) {
        return Member.builder()
                .email(reqDTO.email())
                .password(encryptedPassword)
                .name(reqDTO.name())
                .nickname(reqDTO.nickname())
                .position(reqDTO.position())
                .phone(reqDTO.phone())
                .role(reqDTO.role())
                .profileImage(reqDTO.profileImageUrl())
                .build();
    }

    public static MemberCreateResDTO toMemberCreateResDTO(String memberId) {
        return MemberCreateResDTO.builder()
                .memberId(memberId)
                .build();
    }

    public static MemberInfoResDTO toMemberInfoResDTO(Member member) {
        return MemberInfoResDTO.builder()
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .position(member.getPosition())
                .phone(member.getPhone())
                .role(member.getRole().getDescription())
                .profileImage(member.getProfileImage())
                .build();
    }
}
