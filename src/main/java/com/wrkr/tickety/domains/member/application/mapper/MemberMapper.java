package com.wrkr.tickety.domains.member.application.mapper;

import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequestForExcel;
import com.wrkr.tickety.domains.member.application.dto.response.ManagerGetAllManagerResponse;
import com.wrkr.tickety.domains.member.application.dto.response.ManagerGetAllManagerResponse.Managers;
import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoPreviewResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public static Member mapToMemberFromExcel(
        MemberCreateRequestForExcel memberCreateRequestForExcel,
        String encryptedPassword,
        String profileImageUrl
    ) {
        return Member.builder()
            .email(memberCreateRequestForExcel.getEmail())
            .password(encryptedPassword)
            .name(memberCreateRequestForExcel.getName())
            .nickname(memberCreateRequestForExcel.getNickname())
            .department(memberCreateRequestForExcel.getDepartment())
            .position(memberCreateRequestForExcel.getPosition())
            .phone(memberCreateRequestForExcel.getPhone())
            .role(memberCreateRequestForExcel.getRole())
            .profileImage(profileImageUrl)
            .build();
    }

    public static Member mapToMember(
        MemberCreateRequest memberCreateRequest,
        String encryptedPassword,
        String profileImageUrl
    ) {
        return Member.builder()
            .email(memberCreateRequest.email())
            .password(encryptedPassword)
            .name(memberCreateRequest.name())
            .nickname(memberCreateRequest.nickname())
            .department(memberCreateRequest.department())
            .position(memberCreateRequest.position())
            .phone(memberCreateRequest.phone())
            .role(memberCreateRequest.role())
            .profileImage(profileImageUrl)
            .build();
    }

    public static MemberPkResponse toMemberPkResponse(String memberId) {
        return MemberPkResponse.builder()
            .memberId(memberId)
            .build();
    }

    public static MemberInfoResponse mapToMemberInfoResponse(Member member) {
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
            .agitUrl(member.getAgitUrl())
            .build();
    }

    public static MemberInfoPreviewResponse mapToMemberInfoPreviewResponse(Member member) {
        return MemberInfoPreviewResponse.builder()
            .memberId(PkCrypto.encrypt(member.getMemberId()))
            .profileImage(member.getProfileImage())
            .nickname(member.getNickname())
            .name(member.getName())
            .department(member.getDepartment())
            .position(member.getPosition())
            .phone(member.getPhone())
            .email(member.getEmail())
            .build();
    }

    public static ManagerGetAllManagerResponse toManagerGetAllManagerResponse(Member member, List<Member> allManagers, Map<Long, Long> inProgressTicketCount) {
        return ManagerGetAllManagerResponse.builder()
            .principal(
                toManagerResponse(member, inProgressTicketCount)
            )
            .managers(
                allManagers.stream()
                    .filter(manager -> !manager.getMemberId().equals(member.getMemberId()))
                    .map(manager -> toManagerResponse(manager, inProgressTicketCount))
                    .toList()
            )
            .build();
    }

    public static Managers toManagerResponse(Member member, Map<Long, Long> inProgressTicketCount) {
        return Managers.builder()
            .memberId(PkCrypto.encrypt(member.getMemberId()))
            .email(member.getEmail())
            .profileUrl(member.getProfileImage())
            .nickname(member.getNickname())
            .position(member.getPosition())
            .phoneNumber(member.getPhone())
            .ticketAmount(inProgressTicketCount.getOrDefault(member.getMemberId(), 0L))
            .build();
    }
}
