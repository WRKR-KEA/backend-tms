package com.wrkr.tickety.domains.member.application.usecase;

import static com.wrkr.tickety.common.fixture.member.UserFixture.USER_J;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_EMAIL;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_NICKNAME;
import static com.wrkr.tickety.global.response.code.CommonErrorCode.EXCEED_MAX_FILE_SIZE;
import static com.wrkr.tickety.global.response.code.CommonErrorCode.INVALID_IMAGE_EXTENSION;
import static com.wrkr.tickety.infrastructure.email.EmailConstants.TEMP_PASSWORD_SUBJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.wrkr.tickety.domains.attachment.domain.service.S3ApiService;
import com.wrkr.tickety.domains.auth.utils.PasswordEncoderUtil;
import com.wrkr.tickety.domains.member.application.dto.request.MemberCreateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.mapper.EmailMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberSaveService;
import com.wrkr.tickety.domains.member.presentation.util.validator.MemberFieldValidator;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import com.wrkr.tickety.global.utils.RandomCodeGenerator;
import com.wrkr.tickety.infrastructure.email.EmailCreateRequest;
import com.wrkr.tickety.infrastructure.email.EmailUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("회원 등록 UseCase Layer Test")
class MemberCreateUseCaseTest {

    @InjectMocks
    private MemberCreateUseCase memberCreateUseCase;

    @Mock
    private S3ApiService s3ApiService;

    @Mock
    private MemberSaveService memberSaveService;

    @Mock
    private EmailUtil emailUtil;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private MemberFieldValidator memberFieldValidator;

    @Mock
    private PkCrypto pkCrypto;

    private static MockedStatic<RandomCodeGenerator> randomCodeGeneratorMockedStatic;

    private static MockedStatic<PasswordEncoderUtil> passwordEncoderUtilMockedStatic;

    private MockedStatic<EmailMapper> emailMapperMockedStatic;

    @BeforeEach
    public void setUp() {
        pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();

        randomCodeGeneratorMockedStatic = mockStatic(RandomCodeGenerator.class);
        passwordEncoderUtilMockedStatic = mockStatic(PasswordEncoderUtil.class);
        emailMapperMockedStatic = mockStatic(EmailMapper.class);
    }

    @AfterEach
    public void tearDown() {
        randomCodeGeneratorMockedStatic.close();
        passwordEncoderUtilMockedStatic.close();
        emailMapperMockedStatic.close();
    }


    @DisplayName("새로운 회원이 생성되면, 임시 비밀번호가 이메일로 전송된다.")
    @Test
    void createMemberSuccessTest() {
        //given
        Member member = USER_J.toMember();
        String encryptedMemberId = pkCrypto.encryptValue(member.getMemberId());
        String tempPassword = "Abcd1234567!";
        String encodedPassword = member.getPassword();

        MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
            .email(member.getEmail())
            .name(member.getName())
            .nickname(member.getNickname())
            .department(member.getDepartment())
            .position(member.getPosition())
            .phone(member.getPhone())
            .role(member.getRole())
            .agitUrl(member.getAgitUrl())
            .build();

        MockMultipartFile profileImage = new MockMultipartFile(
            "profileImage",
            "profile.png",
            MediaType.IMAGE_PNG_VALUE,
            "profile".getBytes()
        );

        given(memberSaveService.save(any(Member.class))).willReturn(member);

        EmailCreateRequest emailCreateRequest = EmailCreateRequest.builder()
            .to(member.getEmail())
            .subject(TEMP_PASSWORD_SUBJECT)
            .message(null)
            .build();

        randomCodeGeneratorMockedStatic.when(() -> RandomCodeGenerator.generateUUID().substring(0, 12))
            .thenReturn(tempPassword);

        passwordEncoderUtilMockedStatic.when(() -> PasswordEncoderUtil.encodePassword(any(String.class)))
            .thenReturn(encodedPassword);

        given(memberGetService.existsByEmailAndIsDeleted(member.getEmail(), false)).willReturn(false);
        given(memberGetService.existsByNickname(member.getNickname())).willReturn(false);

        // 정상적인 검증 로직 mocking
        doNothing().when(memberFieldValidator).validateEmailDuplicate(member.getEmail());
        doNothing().when(memberFieldValidator).validateNicknameDuplicate(member.getNickname());

        given(s3ApiService.uploadMemberProfileImage(profileImage)).willReturn(member.getProfileImage());

        given(memberSaveService.save(any(Member.class))).willReturn(member);

        emailMapperMockedStatic.when(() -> EmailMapper.toEmailCreateRequest(
                eq(member.getEmail()),
                eq(TEMP_PASSWORD_SUBJECT),
                any()
            ))
            .thenReturn(emailCreateRequest);

        //when
        MemberPkResponse response = memberCreateUseCase.createMember(memberCreateRequest, profileImage);

        //then
        assertThat(response.memberId()).isEqualTo(encryptedMemberId);
        verify(emailUtil, times(1)).sendMail(eq(emailCreateRequest), eq(tempPassword), any(String.class));
    }


    @DisplayName("등록하려는 회원의 이메일이 이미 사용중일 경우 예외를 발생시킨다.")
    @Test
    void MemberCreateUseCaseTestWithDuplicateEmailException() {
        //given
        Member member = USER_J.toMember();

        MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
            .email(member.getEmail())
            .nickname(member.getNickname())
            .build();

        randomCodeGeneratorMockedStatic.when(RandomCodeGenerator::generateUUID)
            .thenReturn("UniqueUUID12");

        given(memberGetService.existsByEmailAndIsDeleted(member.getEmail(), false)).willReturn(true);
        given(memberGetService.existsByNickname(member.getNickname())).willReturn(false);
        doThrow(ApplicationException.from(ALREADY_EXIST_EMAIL))
            .when(memberFieldValidator).validateEmailDuplicate(member.getEmail());

        //when & then
        assertThatThrownBy(() -> {
            memberCreateUseCase.createMember(memberCreateRequest, null);
        }).isInstanceOf(ApplicationException.class)
            .hasMessageContaining(ALREADY_EXIST_EMAIL.getMessage());
    }

    @DisplayName("등록하려는 회원의 아이디(닉네임)가 이미 사용중일 경우 예외를 발생시킨다.")
    @Test
    void MemberCreateUseCaseTestWithDuplicateNicknameException() {
        //given
        Member member = USER_J.toMember();

        MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
            .email(member.getEmail())
            .nickname(member.getNickname())
            .build();

        randomCodeGeneratorMockedStatic.when(RandomCodeGenerator::generateUUID)
            .thenReturn("UniqueUUID12");

        given(memberGetService.existsByEmailAndIsDeleted(member.getEmail(), false)).willReturn(false);
        given(memberGetService.existsByNickname(member.getNickname())).willReturn(true);
        doThrow(ApplicationException.from(ALREADY_EXIST_NICKNAME))
            .when(memberFieldValidator).validateNicknameDuplicate(member.getNickname());

        //when & then
        assertThatThrownBy(() -> {
            memberCreateUseCase.createMember(memberCreateRequest, null);
        }).isInstanceOf(ApplicationException.class)
            .hasMessageContaining(ALREADY_EXIST_NICKNAME.getMessage());
    }

    @DisplayName("잘못된 파일 확장자의 경우 예외를 발생시킨다.")
    @Test
    void createMemberWithInvalidImageExtension() {
        // given
        MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
            .email("test@example.com")
            .nickname("testNickname")
            .build();

        MockMultipartFile invalidImage = new MockMultipartFile(
            "profileImage",
            "profile.txt", // 잘못된 확장자
            MediaType.TEXT_PLAIN_VALUE,
            "profile".getBytes()
        );

        // when & then
        assertThatThrownBy(() -> {
            memberCreateUseCase.createMember(memberCreateRequest, invalidImage);
        }).isInstanceOf(ApplicationException.class)
            .hasMessageContaining(INVALID_IMAGE_EXTENSION.getMessage());
    }

    @DisplayName("파일 크기가 최대 파일 사이즈를 초과한 경우 예외를 발생시킨다.")
    @Test
    void createMemberWithExceedingFileSize() {
        // given
        MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
            .email("test@example.com")
            .nickname("testNickname")
            .build();

        MockMultipartFile largeImage = new MockMultipartFile(
            "profileImage",
            "profile.png",
            MediaType.IMAGE_PNG_VALUE,
            new byte[(int) (11 * 1024 * 1024)]
        );

        // when & then
        assertThatThrownBy(() -> {
            memberCreateUseCase.createMember(memberCreateRequest, largeImage);
        }).isInstanceOf(ApplicationException.class)
            .hasMessageContaining(EXCEED_MAX_FILE_SIZE.getMessage());
    }

}

