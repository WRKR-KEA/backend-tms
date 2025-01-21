package com.wrkr.tickety.domains.member.application.mapper;

import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResponse;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public static Member toMember(MemberCreateRequest reqDTO, String encryptedPassword) {
        return Member.builder()
                .email(reqDTO.email())
                .password(encryptedPassword)
                .name(reqDTO.name())
                .nickname(reqDTO.nickname())
                .position(reqDTO.position())
                .phone(reqDTO.phone())
                .role(reqDTO.role())
                .profileImage(reqDTO.profileImage())
                .build();
    }

    public static MemberPkResponse toMemberPkResponse(String memberId) {
        return MemberPkResponse.builder()
                .memberId(memberId)
                .build();
    }

    public static MemberInfoResponse toMemberInfoResponse(Member member) {
        return MemberInfoResponse.builder()
                .memberId(PkCrypto.encrypt(member.getMemberId()))
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
