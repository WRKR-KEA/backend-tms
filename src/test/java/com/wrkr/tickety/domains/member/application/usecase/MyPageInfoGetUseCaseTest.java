package com.wrkr.tickety.domains.member.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.application.dto.response.MyPageInfoResponse;
import com.wrkr.tickety.domains.member.application.mapper.MyPageMapper;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.infrastructure.redis.RedisService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MyPageInfoGetUseCaseTest {

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private RedisService redisService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MyPageInfoGetUseCase myPageInfoGetUseCase;

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Test
    @DisplayName("정상 케이스 - 캐시에 값이 있을 경우 Redis에서 데이터 조회")
    void getMyPageInfo_CacheHit() throws Exception {
        // given
        Long memberId = 1L;
        String key = "MEMBER_INFO:" + memberId;
        MyPageInfoResponse expectedResponse = new MyPageInfoResponse(
            PkCrypto.encrypt(memberId),
            "nickname",
            "name",
            "010-1234-5678",
            "email@naver.com",
            "position",
            "profileImage",
            Role.USER.getDescription(),
            "https://example.com/agit",
            "department",
            true, true, true, true
        );

        String cachedData = new ObjectMapper().writeValueAsString(expectedResponse);

        given(redisService.getValues(key)).willReturn(Optional.of(cachedData));
        given(objectMapper.readValue(cachedData, MyPageInfoResponse.class)).willReturn(expectedResponse);

        // when
        MyPageInfoResponse response = myPageInfoGetUseCase.getMyPageInfo(memberId);

        // then
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("정상 케이스 - 캐시에 값이 없으면 DB에서 조회 후 Redis에 저장")
    void getMyPageInfo_CacheMiss() throws Exception {
        // given
        Long memberId = 2L;
        String key = "MEMBER_INFO:" + memberId;
        Member member = Member.builder()
            .memberId(memberId)
            .nickname("nickname")
            .password("password")
            .name("name")
            .phone("010-1234-5678")
            .email("email@naver.com")
            .department("department")
            .position("position")
            .profileImage("profileImage")
            .role(Role.USER)
            .isDeleted(false)
            .build();

        MyPageInfoResponse expectedResponse = MyPageMapper.toMyPageInfoResponse(member);
        String jsonResponse = new ObjectMapper().writeValueAsString(expectedResponse);

        given(redisService.getValues(key)).willReturn(Optional.empty());
        given(memberGetService.byMemberId(memberId)).willReturn(member);
        given(objectMapper.writeValueAsString(expectedResponse)).willReturn(jsonResponse);

        // when
        MyPageInfoResponse response = myPageInfoGetUseCase.getMyPageInfo(memberId);

        // then
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("실패 케이스 - 존재하지 않는 회원 ID로 조회 시 예외 발생 (MEMBER_NOT_FOUND)")
    void getMyPageInfo_memberNotFound() {
        // given
        Long invalidMemberId = 999L;
        given(redisService.getValues("MEMBER_INFO:" + invalidMemberId)).willReturn(Optional.empty());
        given(memberGetService.byMemberId(invalidMemberId))
            .willThrow(ApplicationException.from(MemberErrorCode.MEMBER_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> myPageInfoGetUseCase.getMyPageInfo(invalidMemberId))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("실패 케이스 - 삭제된 회원 정보 조회 시 예외 발생 (DELETED_MEMBER)")
    void getMyPageInfo_deletedMember() {
        // given
        Long memberId = 3L;
        Member deletedMember = Member.builder()
            .memberId(memberId)
            .nickname("nickname")
            .password("password")
            .name("name")
            .phone("010-1234-5678")
            .email("email@naver.com")
            .department("department")
            .position("position")
            .profileImage("profileImage")
            .role(Role.USER)
            .isDeleted(true)
            .build();

        given(redisService.getValues("MEMBER_INFO:" + memberId)).willReturn(Optional.empty());
        given(memberGetService.byMemberId(memberId)).willReturn(deletedMember);

        // when & then
        assertThatThrownBy(() -> myPageInfoGetUseCase.getMyPageInfo(memberId))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(MemberErrorCode.DELETED_MEMBER.getMessage());
    }

    @Test
    @DisplayName("실패 케이스 - 캐시 저장 중 예외 발생")
    void getMyPageInfo_CacheSaveFailure() throws Exception {
        // given
        Long memberId = 4L;
        String key = "MEMBER_INFO:" + memberId;
        Member member = Member.builder()
            .memberId(memberId)
            .nickname("nickname")
            .password("password")
            .name("name")
            .phone("010-1234-5678")
            .email("email@gachon.ac.kr")
            .department("department")
            .position("position")
            .profileImage("profileImage")
            .role(Role.USER)
            .isDeleted(false)
            .build();

        MyPageInfoResponse response = MyPageMapper.toMyPageInfoResponse(member);
        given(redisService.getValues(key)).willReturn(Optional.empty());
        given(memberGetService.byMemberId(memberId)).willReturn(member);
        given(objectMapper.writeValueAsString(response)).willThrow(new JsonProcessingException("직렬화 실패") {
        });

        // when & then
        assertThatThrownBy(() -> myPageInfoGetUseCase.getMyPageInfo(memberId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("캐싱 데이터를 직렬화하는 중 오류 발생");
    }

    @Test
    @DisplayName("실패 케이스 - 캐시 역직렬화 중 예외 발생")
    void getMyPageInfo_CacheDeserializationFailure() throws Exception {
        // given
        Long memberId = 5L;
        String key = "MEMBER_INFO:" + memberId;
        String invalidJson = "{invalid_json}";

        given(redisService.getValues(key)).willReturn(Optional.of(invalidJson));
        given(objectMapper.readValue(invalidJson, MyPageInfoResponse.class)).willThrow(new JsonProcessingException("역직렬화 실패") {
        });

        // when & then
        assertThatThrownBy(() -> myPageInfoGetUseCase.getMyPageInfo(memberId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("캐싱된 데이터를 역직렬화하는 중 오류 발생");
    }
}
