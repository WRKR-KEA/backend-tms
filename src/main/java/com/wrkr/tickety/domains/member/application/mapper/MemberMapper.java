package com.wrkr.tickety.domains.member.application.mapper;

import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.ManagerGetAllManagerResponse;
import com.wrkr.tickety.domains.member.application.dto.response.ManagerGetAllManagerResponse.Managers;
import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public static Member toMember(
        MemberCreateRequest memberCreateRequest,
        String encryptedPassword
    ) {
        return Member.builder()
            .email(memberCreateRequest.getEmail())
            .password(encryptedPassword)
            .name(memberCreateRequest.getName())
            .nickname(memberCreateRequest.getNickname())
            .department(memberCreateRequest.getDepartment())
            .position(memberCreateRequest.getPosition())
            .phone(memberCreateRequest.getPhone())
            .role(memberCreateRequest.getRole())
            .profileImage(memberCreateRequest.getProfileImage())
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
            .department(member.getDepartment())
            .position(member.getPosition())
            .phone(member.getPhone())
            .role(member.getRole().name())
            .profileImage(member.getProfileImage())
            .build();
    }

    public static ManagerGetAllManagerResponse toManagerGetAllManagerResponse(List<Member> allManagers, Map<Long, Long> inProgressTicketCount) {
        return ManagerGetAllManagerResponse.builder()
            .managers(
                allManagers.stream()
                    .map(manager -> ManagerGetAllManagerResponse.Managers.builder()
                        .memberId(PkCrypto.encrypt(manager.getMemberId()))
                        .email(manager.getEmail())
                        .nickname(manager.getNickname())
                        .position(manager.getPosition())
                        .phoneNumber(manager.getPhone())
                        .ticketAmount(inProgressTicketCount.getOrDefault(manager.getMemberId(), 0L))
                        .build()
                    ).toList()
            )
            .build();
    }

    public static Managers toManagerResponse(Member member, Map<Long, Long> inProgressTicketCount) {
        return Managers.builder()
            .memberId(PkCrypto.encrypt(member.getMemberId()))
            .email(member.getEmail())
            .nickname(member.getNickname())
            .position(member.getPosition())
            .phoneNumber(member.getPhone())
            .ticketAmount(inProgressTicketCount.getOrDefault(member.getMemberId(), 0L))
            .build();
    }
}
