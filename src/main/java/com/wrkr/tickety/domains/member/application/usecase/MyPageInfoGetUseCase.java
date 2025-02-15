package com.wrkr.tickety.domains.member.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.application.dto.response.MyPageInfoResponse;
import com.wrkr.tickety.domains.member.application.mapper.MyPageMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageInfoGetUseCase {

    private final MemberGetService memberGetService;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public MyPageInfoResponse getMyPageInfo(Long memberId) {
        String key = "MEMBER_INFO:" + memberId;
        return redisService.getValues(key)
            .map(cachedData -> {
                try {
                    return objectMapper.readValue(cachedData, MyPageInfoResponse.class);
                } catch (Exception e) {
                    throw new RuntimeException("캐싱된 데이터를 역직렬화하는 중 오류 발생", e);
                }
            })
            .orElseGet(() -> {
                Member findMember = memberGetService.byMemberId(memberId);
                if (findMember.isDeleted()) {
                    throw ApplicationException.from(MemberErrorCode.DELETED_MEMBER);
                }

                MyPageInfoResponse response = MyPageMapper.toMyPageInfoResponse(findMember);

                try {
                    String jsonData = objectMapper.writeValueAsString(response);
                    redisService.setValuesWithoutTTL(key, jsonData);
                } catch (Exception e) {
                    throw new RuntimeException("캐싱 데이터를 직렬화하는 중 오류 발생", e);
                }

                return response;
            });
    }
}
