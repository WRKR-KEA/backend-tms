package com.wrkr.tickety.domains.member.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.application.dto.request.MyPageInfoUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MyPageInfoResponse;
import com.wrkr.tickety.domains.member.application.mapper.MyPageMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberUpdateService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class MyPageInfoUpdateUseCase {

    private final MemberGetService memberGetService;
    private final MemberUpdateService memberUpdateService;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public MemberPkResponse updateMyPageInfo(Long memberId, MyPageInfoUpdateRequest request) {
        Member member = memberGetService.byMemberId(memberId);

        if (member.isDeleted()) {
            throw ApplicationException.from(MemberErrorCode.DELETED_MEMBER);
        }

        if (!member.getEmail().equals(request.email()) && memberGetService.existsByEmail(request.email())) {
            throw ApplicationException.from(MemberErrorCode.ALREADY_EXIST_EMAIL);
        }

        member.modifyMyPageInfo(request);
        Member modifiedMember = memberUpdateService.modifyMemberInfo(member);

        String key = "MEMBER_INFO:" + memberId;
        redisService.deleteValues(key);

        try {
            MyPageInfoResponse updatedResponse = MyPageMapper.toMyPageInfoResponse(modifiedMember);
            String jsonData = objectMapper.writeValueAsString(updatedResponse);
            redisService.setValuesWithoutTTL(key, jsonData);
        } catch (Exception e) {
            throw new RuntimeException("회원 정보 캐싱 중 오류 발생", e);
        }

        return MemberPkResponse.builder()
            .memberId(PkCrypto.encrypt(modifiedMember.getMemberId()))
            .build();
    }
}
