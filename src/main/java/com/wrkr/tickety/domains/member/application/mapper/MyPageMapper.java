package com.wrkr.tickety.domains.member.application.mapper;

import com.wrkr.tickety.domains.member.application.dto.response.MyPageInfoResponse;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.utils.PkCrypto;
import org.springframework.stereotype.Component;

@Component
public class MyPageMapper {

    public static MyPageInfoResponse toMyPageInfoResponse(Member member) {
        return MyPageInfoResponse.builder()
            .memberId(PkCrypto.encrypt(member.getMemberId()))
            .email(member.getEmail())
            .name(member.getName())
            .nickname(member.getNickname())
            .position(member.getPosition())
            .phone(member.getPhone())
            .role(member.getRole().getDescription())
            .profileImage(member.getProfileImage())
            .agitUrl(member.getAgitUrl())
            .agitNotification(member.getAgitNotification())
            .emailNotification(member.getEmailNotification())
            .serviceNotification(member.getServiceNotification())
            .build();
    }
}
