package com.wrkr.tickety.domains.member.application.mapper;

import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoPagingResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MemberInfoResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public static Member toMember(
        MemberCreateRequest memberCreateRequest,
        String encryptedPassword
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
            .profileImage(memberCreateRequest.profileImage())
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
            .role(member.getRole().getDescription())
            .profileImage(member.getProfileImage())
            .build();
    }

    public static MemberInfoPagingResponse toMemberInfoPagingResponse(
        List<MemberInfoResponse> memberInfoResponseList,
        int currentPage,
        int totalPages,
        long totalElements,
        int size
    ) {
        return MemberInfoPagingResponse.builder()
            .members(memberInfoResponseList)
            .currentPage(currentPage)
            .totalPages(totalPages)
            .totalElements(totalElements)
            .size(size)
            .build();
    }

    public static List<MemberInfoResponse> toMemberInfoResponseList(List<Member> memberList) {
        return memberList.stream()
            .map(MemberMapper::toMemberInfoResponse)
            .toList();
    }
}
