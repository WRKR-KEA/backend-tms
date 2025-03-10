package com.wrkr.tickety.domains.member.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.application.dto.request.MyPageInfoUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.dto.response.MyPageInfoResponse;
import com.wrkr.tickety.domains.member.application.mapper.MyPageMapper;
import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberUpdateService;
import com.wrkr.tickety.domains.member.exception.MemberErrorCode;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.infrastructure.redis.RedisService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MyPageInfoUpdateUseCaseTest {

    private static final Long USER_ID = 1L;
    private static final Long MANAGER_ID = 2L;
    private static final Long INVALID_ID = 999L;

    private Member user;
    private Member deletedUser;
    private MyPageInfoUpdateRequest validRequest;
    private MyPageInfoUpdateRequest duplicateEmailRequest;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private MemberUpdateService memberUpdateService;

    @Mock
    private RedisService redisService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MyPageInfoUpdateUseCase myPageInfoUpdateUseCase;

    @BeforeEach
    void setUp() {
        user = Member.builder()
            .memberId(USER_ID)
            .password("password")
            .nickname("사용자")
            .email("user@naver.com")
            .phone("010-1234-5678")
            .name("사용자")
            .role(Role.USER)
            .isDeleted(false)
            .build();

        deletedUser = Member.builder()
            .memberId(3L)
            .nickname("삭제된 사용자")
            .isDeleted(true)
            .build();

        validRequest = MyPageInfoUpdateRequest.builder()
            .name("newName")
            .phone("010-9876-5432")
            .email("newEmail@naver.com")
            .department("newDepartment")
            .position("newPosition")
            .agitUrl("newAgitUrl")
            .agitNotification(true)
            .emailNotification(true)
            .serviceNotification(true)
            .kakaoworkNotification(true)
            .build();

        duplicateEmailRequest = MyPageInfoUpdateRequest.builder()
            .name("newName")
            .phone("010-9876-5432")
            .email("duplicate@naver.com") // 중복 이메일 요청
            .department("newDepartment")
            .position("newPosition")
            .agitUrl("newAgitUrl")
            .agitNotification(true)
            .emailNotification(true)
            .serviceNotification(true)
            .kakaoworkNotification(true)
            .build();
    }

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Test
    @DisplayName("정상 케이스 - 회원 정보를 성공적으로 수정한다.")
    void updateMyPageInfo_success() throws JsonProcessingException {
        // given
        given(memberGetService.byMemberId(USER_ID)).willReturn(user);
        given(memberUpdateService.modifyMemberInfo(any(Member.class))).willReturn(user);

        MyPageInfoResponse updatedResponse = MyPageMapper.toMyPageInfoResponse(user);
        String jsonData = new ObjectMapper().writeValueAsString(updatedResponse);

        given(objectMapper.writeValueAsString(any(MyPageInfoResponse.class))).willReturn(jsonData);

        // when
        MemberPkResponse response = myPageInfoUpdateUseCase.updateMyPageInfo(USER_ID, validRequest);

        // then
        assertThat(response.memberId()).isEqualTo(PkCrypto.encrypt(USER_ID));
    }

    @Test
    @DisplayName("실패 케이스 - 존재하지 않는 회원 ID로 수정 시 예외 발생 (MEMBER_NOT_FOUND)")
    void updateMyPageInfo_memberNotFound() {
        // given
        given(memberGetService.byMemberId(INVALID_ID))
            .willThrow(ApplicationException.from(MemberErrorCode.MEMBER_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> myPageInfoUpdateUseCase.updateMyPageInfo(INVALID_ID, validRequest))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("실패 케이스 - 삭제된 회원 정보 수정 시 예외 발생 (DELETED_MEMBER)")
    void updateMyPageInfo_deletedMember() {
        // given
        given(memberGetService.byMemberId(deletedUser.getMemberId())).willReturn(deletedUser);

        // when & then
        assertThatThrownBy(() -> myPageInfoUpdateUseCase.updateMyPageInfo(deletedUser.getMemberId(), validRequest))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(MemberErrorCode.DELETED_MEMBER.getMessage());
    }

    @Test
    @DisplayName("실패 케이스 - 이메일이 이미 존재할 경우 예외 발생 (ALREADY_EXIST_EMAIL)")
    void updateMyPageInfo_duplicateEmail() {
        // given
        given(memberGetService.byMemberId(USER_ID)).willReturn(user);
        given(memberGetService.existsByEmailAndIsDeleted(duplicateEmailRequest.email(), false)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> myPageInfoUpdateUseCase.updateMyPageInfo(USER_ID, duplicateEmailRequest))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(MemberErrorCode.ALREADY_EXIST_EMAIL.getMessage());
    }

    @Test
    @DisplayName("실패 케이스 - 캐시 저장 중 예외 발생")
    void updateMyPageInfo_CacheSaveFailure() throws Exception {
        // given
        given(memberGetService.byMemberId(USER_ID)).willReturn(user);
        given(memberUpdateService.modifyMemberInfo(any(Member.class))).willReturn(user);
        given(objectMapper.writeValueAsString(any(MyPageInfoResponse.class))).willThrow(new JsonProcessingException("직렬화 실패") {
        });

        // when & then
        assertThatThrownBy(() -> myPageInfoUpdateUseCase.updateMyPageInfo(USER_ID, validRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("회원 정보 캐싱 중 오류 발생");
    }

    @Test
    @DisplayName("실패 케이스 - 캐시 삭제 중 예외 발생")
    void updateMyPageInfo_CacheDeleteFailure() throws Exception {
        // given
        given(memberGetService.byMemberId(USER_ID)).willReturn(user);
        given(memberUpdateService.modifyMemberInfo(any(Member.class))).willReturn(user);
        doThrow(new RuntimeException("Redis 삭제 실패")).when(redisService).deleteValues("MEMBER_INFO:" + USER_ID);

        // when & then
        assertThatThrownBy(() -> myPageInfoUpdateUseCase.updateMyPageInfo(USER_ID, validRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Redis 삭제 실패");
    }
}
