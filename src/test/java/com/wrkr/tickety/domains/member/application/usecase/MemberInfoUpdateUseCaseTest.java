package com.wrkr.tickety.domains.member.application.usecase;

import static com.wrkr.tickety.common.fixture.member.UserFixture.ADMIN_A;
import static com.wrkr.tickety.common.fixture.member.UserFixture.ADMIN_B;
import static com.wrkr.tickety.common.fixture.member.UserFixture.USER_J;
import static com.wrkr.tickety.common.fixture.member.UserFixture.USER_K;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_EMAIL;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_NICKNAME;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.wrkr.tickety.global.response.code.CommonErrorCode.EXCEED_MAX_FILE_SIZE;
import static com.wrkr.tickety.global.response.code.CommonErrorCode.INVALID_IMAGE_EXTENSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.attachment.domain.service.S3ApiService;
import com.wrkr.tickety.domains.auth.exception.AuthErrorCode;
import com.wrkr.tickety.domains.member.application.dto.request.MemberInfoUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberUpdateService;
import com.wrkr.tickety.domains.member.presentation.util.validator.MemberFieldValidator;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.infrastructure.redis.RedisService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MemberInfoUpdateUseCaseTest {

    @InjectMocks
    private MemberInfoUpdateUseCase memberInfoUpdateUseCase;

    @Mock
    private MemberUpdateService memberUpdateService;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private S3ApiService s3ApiService;

    @Mock
    private MemberFieldValidator memberFieldValidator;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RedisService redisService;

    @Mock
    private PkCrypto pkCrypto;

    @BeforeEach
    public void setUp() {
        pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @Nested
    @DisplayName("ModifyMemberInfo UseCase Layer > 회원 등록")
    class ModifyMemberInfo {

        @DisplayName("회원 정보를 성공적으로 수정하면 암호화된 회원의 PK가 반환된다.")
        @Test
        void modifyMemberInfoSuccessTest() throws Exception {
            // given
            Member existingMember = USER_J.toMember();
            Member newMemberInfo = USER_K.toMember();

            String encryptedMemberId = pkCrypto.encryptValue(existingMember.getMemberId());

            MemberInfoUpdateRequest request = MemberInfoUpdateRequest.builder()
                .email(newMemberInfo.getEmail())
                .name(newMemberInfo.getName())
                .nickname(newMemberInfo.getNickname())
                .department(newMemberInfo.getDepartment())
                .position(newMemberInfo.getPosition())
                .phone(newMemberInfo.getPhone())
                .agitUrl(newMemberInfo.getAgitUrl())
                .build();

            MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "new-profile.png",
                MediaType.IMAGE_PNG_VALUE,
                "new-profile".getBytes()
            );

            Member newMember = Member.builder()
                .memberId(existingMember.getMemberId())
                .email(newMemberInfo.getEmail())
                .password(newMemberInfo.getPassword())
                .name(newMemberInfo.getName())
                .nickname(newMemberInfo.getNickname())
                .department(newMemberInfo.getDepartment())
                .position(newMemberInfo.getPosition())
                .phone(newMemberInfo.getPhone())
                .role(existingMember.getRole())
                .profileImage(profileImage.getOriginalFilename())
                .build();

            given(memberGetService.byMemberId(any())).willReturn(existingMember);

            doNothing().when(memberFieldValidator).validateEmailDuplicate(any());
            doNothing().when(memberFieldValidator).validateNicknameDuplicate(any());

            given(s3ApiService.uploadMemberProfileImage(profileImage)).willReturn(profileImage.getOriginalFilename());

            given(memberUpdateService.modifyMemberInfo(any(Member.class))).willReturn(newMember);

            given(objectMapper.writeValueAsString(any(Object.class))).willReturn(toString());

            doNothing().when(redisService).deleteValues(any(String.class));
            doNothing().when(redisService).setValuesWithoutTTL(any(String.class), any(String.class));

            // when
            MemberPkResponse response = memberInfoUpdateUseCase.modifyMemberInfo(encryptedMemberId, request, profileImage);

            // then
            assertThat(response.memberId()).isEqualTo(encryptedMemberId);
            verify(redisService, times(1)).deleteValues(any(String.class));
            verify(redisService, times(1)).setValuesWithoutTTL(any(String.class), any(String.class));
        }

        @DisplayName("이메일이 이미 사용 중일 경우 ALREADY_EXIST_EMAIL 예외를 발생시킨다.")
        @Test
        void modifyMemberInfoWithDuplicateEmailException() {
            // given
            Member existingMember = USER_J.toMember();
            Member newMember = USER_K.toMember();

            String encryptedMemberId = pkCrypto.encryptValue(existingMember.getMemberId());

            MemberInfoUpdateRequest request = MemberInfoUpdateRequest.builder()
                .email(newMember.getEmail())
                .name(newMember.getName())
                .nickname(newMember.getNickname())
                .department(newMember.getDepartment())
                .position(newMember.getPosition())
                .phone(newMember.getPhone())
                .agitUrl(newMember.getAgitUrl())
                .build();

            given(memberGetService.byMemberId(any())).willReturn(existingMember);
            doThrow(ApplicationException.from(ALREADY_EXIST_EMAIL))
                .when(memberFieldValidator).validateEmailDuplicate(any());

            // when & then
            assertThatThrownBy(() -> {
                memberInfoUpdateUseCase.modifyMemberInfo(encryptedMemberId, request, null);
            }).isInstanceOf(ApplicationException.class)
                .hasMessageContaining(ALREADY_EXIST_EMAIL.getMessage());
        }

        @DisplayName("닉네임이 이미 사용 중일 경우 ALREADY_EXIST_NICKNAME 예외를 발생시킨다.")
        @Test
        void modifyMemberInfoWithDuplicateNicknameException() {
            // given
            Member existingMember = USER_J.toMember();
            Member newMember = USER_K.toMember();

            String encryptedMemberId = pkCrypto.encryptValue(existingMember.getMemberId());

            MemberInfoUpdateRequest request = MemberInfoUpdateRequest.builder()
                .email(newMember.getEmail())
                .name(newMember.getName())
                .nickname(newMember.getNickname())
                .department(newMember.getDepartment())
                .position(newMember.getPosition())
                .phone(newMember.getPhone())
                .agitUrl(newMember.getAgitUrl())
                .build();

            given(memberGetService.byMemberId(any())).willReturn(existingMember);
            doThrow(ApplicationException.from(ALREADY_EXIST_NICKNAME))
                .when(memberFieldValidator).validateNicknameDuplicate(any());

            // when & then
            assertThatThrownBy(() -> {
                memberInfoUpdateUseCase.modifyMemberInfo(encryptedMemberId, request, null);
            }).isInstanceOf(ApplicationException.class)
                .hasMessageContaining(ALREADY_EXIST_NICKNAME.getMessage());
        }

        @DisplayName("잘못된 파일 확장자의 경우 INVALID_IMAGE_EXTENSION 예외를 발생시킨다.")
        @Test
        void modifyMemberInfoWithInvalidImageExtension() {
            // given
            Member existingMember = USER_J.toMember();
            Member newMember = USER_K.toMember();

            String encryptedMemberId = pkCrypto.encryptValue(existingMember.getMemberId());

            MemberInfoUpdateRequest request = MemberInfoUpdateRequest.builder()
                .email(newMember.getEmail())
                .name(newMember.getName())
                .nickname(newMember.getNickname())
                .department(newMember.getDepartment())
                .position(newMember.getPosition())
                .phone(newMember.getPhone())
                .agitUrl(newMember.getAgitUrl())
                .build();

            MockMultipartFile invalidImage = new MockMultipartFile(
                "profileImage",
                "profile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "profile".getBytes()
            );

            // when & then
            assertThatThrownBy(() -> {
                memberInfoUpdateUseCase.modifyMemberInfo(encryptedMemberId, request, invalidImage);
            }).isInstanceOf(ApplicationException.class)
                .hasMessageContaining(INVALID_IMAGE_EXTENSION.getMessage());
        }

        @DisplayName("파일 크기가 최대 파일 사이즈를 초과한 경우 EXCEED_MAX_FILE_SIZE 예외를 발생시킨다.")
        @Test
        void modifyMemberInfoWithExceedingFileSize() {
            // given
            Member existingMember = USER_J.toMember();
            Member newMember = USER_K.toMember();

            String encryptedMemberId = pkCrypto.encryptValue(existingMember.getMemberId());

            MemberInfoUpdateRequest request = MemberInfoUpdateRequest.builder()
                .email(newMember.getEmail())
                .name(newMember.getName())
                .nickname(newMember.getNickname())
                .department(newMember.getDepartment())
                .position(newMember.getPosition())
                .phone(newMember.getPhone())
                .agitUrl(newMember.getAgitUrl())
                .build();

            MockMultipartFile largeImage = new MockMultipartFile(
                "profileImage",
                "profile.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[(int) (11 * 1024 * 1024)]
            );

            // when & then
            assertThatThrownBy(() -> {
                memberInfoUpdateUseCase.modifyMemberInfo(encryptedMemberId, request, largeImage);
            }).isInstanceOf(ApplicationException.class)
                .hasMessageContaining(EXCEED_MAX_FILE_SIZE.getMessage());
        }

        @DisplayName("변경하려는 회원의 권한이 ADMIN인 경우 PERMISSION_DENIED 예외를 발생시킨다.")
        @Test
        void modifyMemberInfoWithAdminAuthorizationException() {
            // given
            Member adminMember = ADMIN_A.toMember();
            Member newMember = ADMIN_B.toMember();

            String encryptedMemberId = pkCrypto.encryptValue(adminMember.getMemberId());

            MemberInfoUpdateRequest request = MemberInfoUpdateRequest.builder()
                .email(newMember.getEmail())
                .name(newMember.getName())
                .nickname(newMember.getNickname())
                .department(newMember.getDepartment())
                .position(newMember.getPosition())
                .phone(newMember.getPhone())
                .agitUrl(newMember.getAgitUrl())
                .build();

            given(memberGetService.byMemberId(any())).willReturn(adminMember);

            // when & then
            assertThatThrownBy(() -> {
                memberInfoUpdateUseCase.modifyMemberInfo(encryptedMemberId, request, null);
            }).isInstanceOf(ApplicationException.class)
                .hasMessageContaining(AuthErrorCode.PERMISSION_DENIED.getMessage());
        }
    }

    @Nested
    @DisplayName("ModifyMemberInfo UseCase Layer > 회원 등록")
    class SoftDeleteMember {

        @DisplayName("회원 삭제가 실행되면 캐시 및 프로필 이미지를 삭제하고, isDeleted가 true로 변경된다.")
        @Test
        void softDeleteMemberSuccess() {
            // given
            Member existingMember = USER_J.toMember();

            String encryptedMemberId = pkCrypto.encryptValue(existingMember.getMemberId());
            List<String> memberIdList = List.of(encryptedMemberId);

            given(memberGetService.byMemberId(any())).willReturn(existingMember);
            doNothing().when(redisService).deleteValues(any(String.class));
            given(memberUpdateService.modifyMemberInfo(any(Member.class))).willReturn(existingMember);

            // when
            memberInfoUpdateUseCase.softDeleteMember(memberIdList);

            // then
            verify(redisService, times(1)).deleteValues("MEMBER_INFO:" + existingMember.getMemberId());
            verify(s3ApiService, times(1)).deleteObject(existingMember.getProfileImage());
            verify(memberUpdateService, times(1)).modifyMemberInfo(existingMember);
            assertThat(existingMember.isDeleted()).isTrue();
        }

        @DisplayName("삭제하려는 회원이 존재하지 않는 경우 MEMBER_NOT_FOUND 예외를 발생시킨다.")
        @Test
        void softDeleteMemberWithNonExistentMember() {
            // given
            String nonExistentEncryptedId = pkCrypto.encryptValue(USER_J.toMember().getMemberId());
            List<String> memberIdList = List.of(nonExistentEncryptedId);

            given(memberGetService.byMemberId(any())).willThrow(ApplicationException.from(MEMBER_NOT_FOUND));

            // when & then
            assertThatThrownBy(() -> {
                memberInfoUpdateUseCase.softDeleteMember(memberIdList);
            }).isInstanceOf(ApplicationException.class)
                .hasMessageContaining(MEMBER_NOT_FOUND.getMessage());
        }

        @DisplayName("삭제하려는 회원이 ADMIN인 경우 PERMISSION_DENIED 예외를 발생시킨다.")
        @Test
        void softDeleteMemberWithAdminAuthorizationException() {
            // given
            Member adminMember = ADMIN_A.toMember();
            String encryptedAdminId = pkCrypto.encryptValue(adminMember.getMemberId());
            List<String> memberIdList = List.of(encryptedAdminId);

            given(memberGetService.byMemberId(any())).willReturn(adminMember);

            // when & then
            assertThatThrownBy(() -> {
                memberInfoUpdateUseCase.softDeleteMember(memberIdList);
            }).isInstanceOf(ApplicationException.class)
                .hasMessageContaining(AuthErrorCode.PERMISSION_DENIED.getMessage());
        }
    }
}
