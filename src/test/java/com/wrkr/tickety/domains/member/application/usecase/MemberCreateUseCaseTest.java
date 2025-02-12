package com.wrkr.tickety.domains.member.application.usecase;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_EMAIL;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_NICKNAME;
import static com.wrkr.tickety.infrastructure.email.EmailConstants.TEMP_PASSWORD_SUBJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.constant.Role;
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

    private static MockedStatic<RandomCodeGenerator> randomCodeGeneratorMockedStatic;

    private static MockedStatic<PasswordEncoderUtil> passwordEncoderUtilMockedStatic;

    private static MockedStatic<PkCrypto> pkCrpytoMockedStatic;

    private MockedStatic<MemberMapper> memberMapperMockedStatic;

    private MockedStatic<EmailMapper> emailMapperMockedStatic;

    @BeforeEach
    public void setUp() {
        randomCodeGeneratorMockedStatic = mockStatic(RandomCodeGenerator.class);
        passwordEncoderUtilMockedStatic = mockStatic(PasswordEncoderUtil.class);
        pkCrpytoMockedStatic = mockStatic(PkCrypto.class);
        memberMapperMockedStatic = mockStatic(MemberMapper.class);
        emailMapperMockedStatic = mockStatic(EmailMapper.class);
    }

    @AfterEach
    public void tearDown() {
        randomCodeGeneratorMockedStatic.close();
        passwordEncoderUtilMockedStatic.close();
        pkCrpytoMockedStatic.close();
        memberMapperMockedStatic.close();
        emailMapperMockedStatic.close();
    }

    @DisplayName("새로운 회원이 생성되면, 임시 비밀번호가 이메일로 전송된다.")
    @Test
    void createMemberSuccessTest() {
        //given
        Long memberId = 1L;
        String encryptedMemberId = "encryptedMemberId";

        String tempPassword = "Abcd1234567!";
        String encodedPassword = "abcd1234!";

        String nickname = "test.user";
        String email = "test@gachon.ac.kr";

        String name = "TestUser";
        String department = "BE team1";
        String position = "BE Developer";
        String phone = "010-1234-5678";
        Role role = Role.USER;
        String profileImageUrl = "profile.png";
        String agitUrl = "https://www.gachon.ac.kr";

        Member createdMember = Member.builder()
            .memberId(memberId)
            .nickname(nickname)
            .password(encodedPassword)
            .email(email)
            .name(name)
            .department(department)
            .position(position)
            .phone(phone)
            .role(role)
            .agitUrl(agitUrl)
            .profileImage(profileImageUrl)
            .build();

        MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
            .email(email)
            .name(name)
            .nickname(nickname)
            .department(department)
            .position(position)
            .phone(phone)
            .role(role)
            .agitUrl(agitUrl)
            .build();

        MockMultipartFile profileImage = new MockMultipartFile(
            "profileImage",
            "profile.png",
            MediaType.IMAGE_PNG_VALUE,
            "profile".getBytes()
        );

        EmailCreateRequest emailCreateRequest = EmailCreateRequest.builder()
            .to(email)
            .subject(TEMP_PASSWORD_SUBJECT)
            .message(null)
            .build();

        MemberPkResponse memberPkResponse = MemberPkResponse.builder()
            .memberId(encryptedMemberId)
            .build();

        randomCodeGeneratorMockedStatic.when(() -> RandomCodeGenerator.generateUUID().substring(0, 12))
            .thenReturn(tempPassword);

        passwordEncoderUtilMockedStatic.when(() -> PasswordEncoderUtil.encodePassword(any(String.class)))
            .thenReturn(encodedPassword);

        given(memberGetService.existsByEmail(email)).willReturn(false);
        given(memberGetService.existsByNickname(nickname)).willReturn(false);

        // 정상적인 검증 로직 mocking
        doNothing().when(memberFieldValidator).validateEmailDuplicate(email);
        doNothing().when(memberFieldValidator).validateNicknameDuplicate(nickname);

        given(s3ApiService.uploadMemberProfileImage(profileImage)).willReturn(profileImageUrl);

        memberMapperMockedStatic.when(() -> MemberMapper.mapToMember(
                    any(MemberCreateRequest.class),
                    any(String.class),
                    any(String.class)
                )
            )
            .thenReturn(createdMember);
        given(memberSaveService.save(any(Member.class))).willReturn(createdMember);

        emailMapperMockedStatic.when(() -> EmailMapper.toEmailCreateRequest(
                eq(email),
                eq(TEMP_PASSWORD_SUBJECT),
                isNull()
            ))
            .thenReturn(emailCreateRequest);

        pkCrpytoMockedStatic.when(() -> PkCrypto.encrypt(any(Long.class)))
            .thenReturn(encryptedMemberId);

        memberMapperMockedStatic.when(() -> MemberMapper.toMemberPkResponse(any(String.class)))
            .thenReturn(memberPkResponse);

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
        String nickname = "test.user";
        String email = "test@gachon.ac.kr";

        MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
            .email(email)
            .nickname(nickname)
            .build();

        randomCodeGeneratorMockedStatic.when(RandomCodeGenerator::generateUUID)
            .thenReturn("UniqueUUID12");

        given(memberGetService.existsByEmail(email)).willReturn(true);
        given(memberGetService.existsByNickname(nickname)).willReturn(false);
        doThrow(ApplicationException.from(ALREADY_EXIST_EMAIL))
            .when(memberFieldValidator).validateEmailDuplicate(email);

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
        String nickname = "test.user";
        String email = "test@gachon.ac.kr";

        MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
            .email(email)
            .nickname(nickname)
            .build();

        randomCodeGeneratorMockedStatic.when(RandomCodeGenerator::generateUUID)
            .thenReturn("UniqueUUID12");

        given(memberGetService.existsByEmail(email)).willReturn(false);
        given(memberGetService.existsByNickname(nickname)).willReturn(true);
        doThrow(ApplicationException.from(ALREADY_EXIST_NICKNAME))
            .when(memberFieldValidator).validateNicknameDuplicate(nickname);

        //when & then
        assertThatThrownBy(() -> {
            memberCreateUseCase.createMember(memberCreateRequest, null);
        }).isInstanceOf(ApplicationException.class)
            .hasMessageContaining(ALREADY_EXIST_NICKNAME.getMessage());
    }
}