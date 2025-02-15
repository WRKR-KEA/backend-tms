package com.wrkr.tickety.domains.member.presentation.util.validator;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_EMAIL;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.ALREADY_EXIST_NICKNAME;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_DEPARTMENT;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_EMAIL;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_NAME;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_NICKNAME;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_PHONE;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_POSITION;
import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_ROLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.global.exception.ApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberFieldValidatorTest {

    @InjectMocks
    private MemberFieldValidator memberFieldValidator;

    @Mock
    private MemberGetService memberGetService;

    @Nested
    class ValidateNicknameTest {

        @DisplayName("사용중인 아이디(닉네임)일 경우 예외가 발생한다.")
        @Test
        void validateNicknameDuplicateTestDuplicate() {
            //given
            String nickname = "nickname.nick";
            given(memberGetService.existsByNickname(nickname)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> memberFieldValidator.validateNicknameDuplicate(nickname))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ALREADY_EXIST_NICKNAME.getMessage())
                .satisfies(exception -> {
                    assertThat(((ApplicationException) exception).getCode()).isEqualTo(ALREADY_EXIST_NICKNAME);
                });
        }

        @DisplayName("사용중인 아이디(닉네임)가 아닐 경우 예외가 발생하지 않는다.")
        @Test
        void validateNicknameDuplicateTestNotDuplicate() {
            //given
            String nickname = "nickname.nick";
            given(memberGetService.existsByNickname(nickname)).willReturn(false);

            // when & then
            memberFieldValidator.validateNicknameDuplicate(nickname);
        }

        @DisplayName("아이디(닉네임)의 형식이 유효하지 않으면 예외가 발생한다.")
        @Test
        void validateNicknameFormatTestInvalid() {
            //given
            String nickname = "nickname";

            //when & then
            assertThatThrownBy(() -> memberFieldValidator.validateNicknameFormat(nickname))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(INVALID_NICKNAME.getMessage())
                .satisfies(exception -> {
                    assertThat(((ApplicationException) exception).getCode()).isEqualTo(INVALID_NICKNAME);
                });
        }

        @DisplayName("아이디(닉네임)의 형식이 유효하면 예외가 발생하지 않는다.")
        @Test
        void validateNicknameFormatTestValid() {
            //given
            String nickname = "nickname.nick";

            //when & then
            memberFieldValidator.validateNicknameFormat(nickname);
        }
    }

    @Nested
    class ValidateEmailTest {

        @DisplayName("사용중인 이메일일 경우 예외가 발생한다.")
        @Test
        void validateEmailDuplicateTestDuplicate() {
            //given
            String email = "email@gachon.ac.kr";
            given(memberGetService.existsByEmailAndIsDeleted(email, false)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> memberFieldValidator.validateEmailDuplicate(email))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ALREADY_EXIST_EMAIL.getMessage())
                .satisfies(exception -> {
                    assertThat(((ApplicationException) exception).getCode()).isEqualTo(ALREADY_EXIST_EMAIL);
                });
        }

        @DisplayName("사용중이지 않은 이메일일 경우 예외가 발생하지 않는다.")
        @Test
        void validateEmailDuplicateTestNotDuplicate() {
            //given
            String email = "email@gachon.ac.kr";
            given(memberGetService.existsByEmailAndIsDeleted(email, false)).willReturn(false);

            //when & then
            memberFieldValidator.validateEmailDuplicate(email);
        }

        @DisplayName("이메일의 형식이 유효하지 않으면 예외가 발생한다.")
        @Test
        void validateEmailFormatTestInvalid() {
            //given
            String email = "email@abc.com";

            //when & then
            assertThatThrownBy(() -> memberFieldValidator.validateEmailFormat(email))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(INVALID_EMAIL.getMessage())
                .satisfies(exception -> {
                    assertThat(((ApplicationException) exception).getCode()).isEqualTo(INVALID_EMAIL);
                });
        }

        @DisplayName("이메일의 형식이 유효하면 예외가 발생하지 않는다.")
        @Test
        void validateEmailFormatTestValid() {
            //given
            String email = "email@gachon.ac.kr";

            //when & then
            memberFieldValidator.validateEmailFormat(email);
        }
    }


    @Nested
    class ValidateNameTest {

        @DisplayName("이름의 형식이 유효하지 않으면 예외가 발생한다.")
        @Test
        void validateNameTestInvalid() {
            //given
            String name = " ";

            //when & then
            assertThatThrownBy(() -> memberFieldValidator.validateName(name))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(INVALID_NAME.getMessage())
                .satisfies(exception -> {
                    assertThat(((ApplicationException) exception).getCode()).isEqualTo(INVALID_NAME);
                });
        }

        @DisplayName("이름의 형식이 유효하면 예외가 발생하지 않는다.")
        @Test
        void validateNameTestValid() {
            //given
            String name = "김기남";

            //when & then
            memberFieldValidator.validateName(name);
        }
    }

    @Nested
    class ValidateDepartmentTest {

        @DisplayName("부서의 형식이 유효하지 않으면 예외가 발생한다.")
        @Test
        void validateDepartmentTestInvalid() {
            //given
            String department = " ";

            //when & then
            assertThatThrownBy(() -> memberFieldValidator.validateDepartment(department))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(INVALID_DEPARTMENT.getMessage())
                .satisfies(exception -> {
                    assertThat(((ApplicationException) exception).getCode()).isEqualTo(INVALID_DEPARTMENT);
                });
        }

        @DisplayName("부서의 형식이 유효하면 예외가 발생하지 않는다.")
        @Test
        void validateDepartmentTestValid() {
            //given
            String department = "개발1팀";

            //when & then
            memberFieldValidator.validateDepartment(department);
        }
    }

    @Nested
    class ValidatePositionTest {

        @DisplayName("직책의 형식이 유효하지 않으면 예외가 발생한다.")
        @Test
        void validatePositionTestInvalid() {
            //given
            String position = " ";

            //when & then
            assertThatThrownBy(() -> memberFieldValidator.validatePosition(position))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(INVALID_POSITION.getMessage())
                .satisfies(exception -> {
                    assertThat(((ApplicationException) exception).getCode()).isEqualTo(INVALID_POSITION);
                });
        }

        @DisplayName("직책의 형식이 유효하면 예외가 발생하지 않는다.")
        @Test
        void validatePositionTestValid() {
            //given
            String position = "백엔드 개발자";

            //when & then
            memberFieldValidator.validatePosition(position);
        }
    }

    @Nested
    class ValidatePhoneTest {

        @DisplayName("전화번호의 형식이 유효하지 않으면 예외가 발생한다.")
        @Test
        void validatePhoneFormatTestInvalid() {
            //given
            String phone = "010-123-321";

            //when & then
            assertThatThrownBy(() -> memberFieldValidator.validatePhoneFormat(phone))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(INVALID_PHONE.getMessage())
                .satisfies(exception -> {
                    assertThat(((ApplicationException) exception).getCode()).isEqualTo(INVALID_PHONE);
                });
        }

        @DisplayName("전화번호의 형식이 유효하면 예외가 발생하지 않는다.")
        @Test
        void validatePhoneFormatTestValid() {
            //given
            String phone = "010-1234-5678";

            //when & then
            memberFieldValidator.validatePhoneFormat(phone);
        }
    }

    @Nested
    class ValidateRoleTest {

        @DisplayName("권한의 형식이 유효하지 않으면 예외가 발생한다.")
        @Test
        void validateRoleTestInvalid() {
            //given
            Role role = null;

            //when & then
            assertThatThrownBy(() -> memberFieldValidator.validateRole(role))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(INVALID_ROLE.getMessage())
                .satisfies(exception -> {
                    assertThat(((ApplicationException) exception).getCode()).isEqualTo(INVALID_ROLE);
                });
        }

        @DisplayName("권한의 형식이 유효하면 예외가 발생하지 않는다.")
        @Test
        void validateRoleTestValid() {
            //given
            Role role = Role.USER;

            //when & then
            memberFieldValidator.validateRole(role);
        }
    }
}