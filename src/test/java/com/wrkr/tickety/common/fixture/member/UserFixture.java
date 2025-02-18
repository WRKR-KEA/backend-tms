package com.wrkr.tickety.common.fixture.member;

import static com.wrkr.tickety.domains.auth.utils.PasswordEncoderUtil.encodePassword;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import lombok.Getter;

@Getter
public enum UserFixture {

    // USER 데이터
    USER_A(1L, "thama@naver.com", "Dummy0212*", "김민형", "thama.wrkr", "INFRA", "팀원", "010-1234-5678", Role.USER, "profile_a.jpg"),
    USER_B(2L, "wonee@naver.com", "Dummy0212*", "김지원", "wonee.wrkr", "INFRA", "팀원", "010-9876-5432", Role.USER, "profile_b.jpg"),
    USER_C(3L, "curi@naver.com", "Dummy0212*", "강수진", "curi.wrkr", "INFRA", "팀원", "010-5678-1234", Role.USER, "profile_c.jpg"),
    USER_D(4L, "yosbi@naver.com", "Dummy0212*", "하재웅", "yosbi.wrkr", "INFRA", "팀원", "010-2468-1357", Role.USER, "profile_d.jpg"),
    USER_E(5L, "tomcat@naver.com", "Dummy0212*", "박선웅", "tomcat.wrkr", "INFRA", "팀원", "010-1131-2222", Role.USER, "profile_e.jpg"),
    USER_F(6L, "terry@naver.com", "Dummy0212*", "한상결", "terry.wrkr", "INFRA", "팀원", "010-1111-2222", Role.USER, "profile_f.jpg"),
    USER_G(7L, "phurray@naver.com", "Dummy0212*", "김민서", "phurray.wrkr", "INFRA", "팀원", "010-1111-2452", Role.USER, "profile_g.jpg"),
    USER_H(8L, "harrison@naver.com", "Dummy0212*", "박정재", "harrison.wrkr", "INFRA", "팀원", "010-7711-2222", Role.USER, "profile_h.jpg"),
    USER_I(9L, "loopy@naver.com", "Dummy0212*", "최예원", "loopy.wrkr", "INFRA", "팀원", "010-1161-2222", Role.USER, "profile_f.jpg"),

    MANAGER_A(11L, "thama@gmail.com", "Dummy0212*", "김민형", "thama.kakao", "BACKEND", "팀원", "010-1234-5618", Role.MANAGER, "profile_a.jpg"),
    MANAGER_B(12L, "wonee@gmail.com", "Dummy0212*", "김지원", "wonee.kakao", "BACKEND", "팀원", "010-9876-5932", Role.MANAGER, "profile_b.jpg"),
    MANAGER_C(13L, "curi@gmail.com", "Dummy0212*", "강수진", "curi.kakao", "BACKEND", "팀원", "010-5678-1664", Role.MANAGER, "profile_c.jpg"),
    MANAGER_D(14L, "yosbi@gmail.com", "Dummy0212*", "하재웅", "yosbi.kakao", "BACKEND", "팀원", "010-2468-1390", Role.MANAGER, "profile_d.jpg"),
    MANAGER_E(15L, "tomcat@gmail.com", "Dummy0212*", "박선웅", "tomcat.kakao", "BACKEND", "팀원", "010-1131-2202", Role.MANAGER, "profile_e.jpg"),
    MANAGER_F(16L, "terry@gmail.com", "Dummy0212*", "한상결", "terry.kakao", "BACKEND", "팀원", "010-1811-2224", Role.MANAGER, "profile_f.jpg"),
    MANAGER_G(17L, "phurray@gmail.com", "Dummy0212*", "김민서", "phurray.kakao", "BACKEND", "팀원", "010-1111-2352", Role.MANAGER, "profile_g.jpg"),
    MANAGER_H(18L, "harrison@gmail.com", "Dummy0212*", "박정재", "harrison.kakao", "BACKEND", "팀원", "010-7211-2234", Role.MANAGER, "profile_h.jpg"),
    MANAGER_I(19L, "loopy@gmail.com", "Dummy0212*", "최예원", "loopy.kakao", "BACKEND", "팀원", "010-1161-2232", Role.MANAGER, "profile_f.jpg"),

    USER_J(20L, "thama@gmail.com", "Dummy0212*", "김민형", "thama.wrkr", "INFRA", "팀원", "010-1234-5678", Role.USER, "profile_a.jpg"),
    USER_K(21L, "wonee@gmail.com", "Dummy0212*", "김지원", "wonee.wrkr", "INFRA", "팀원", "010-9876-5432", Role.USER, "profile_b.jpg"),
    USER_L(22L, "curi@gmail.com", "Dummy0212*", "강수진", "curi.wrkr", "INFRA", "팀원", "010-5678-1234", Role.USER, "profile_c.jpg"),
    USER_M(23L, "yosbi@gmail.com", "Dummy0212*", "하재웅", "yosbi.wrkr", "INFRA", "팀원", "010-2468-1357", Role.USER, "profile_d.jpg"),
    USER_N(24L, "tomcat@gmail.com", "Dummy0212*", "박선웅", "tomcat.wrkr", "INFRA", "팀원", "010-1131-2222", Role.USER, "profile_e.jpg"),
    USER_O(25L, "terry@gmail.com", "Dummy0212*", "한상결", "terry.wrkr", "INFRA", "팀원", "010-1111-2222", Role.USER, "profile_f.jpg"),
    USER_P(26L, "phurray@gmail.com", "Dummy0212*", "김민서", "phurray.wrkr", "INFRA", "팀원", "010-1111-2452", Role.USER, "profile_g.jpg"),
    USER_Q(27L, "harrison@gmail.com", "Dummy0212*", "박정재", "harrison.wrkr", "INFRA", "팀원", "010-7711-2222", Role.USER, "profile_h.jpg"),
    USER_R(28L, "loopy@gmail.com", "Dummy0212*", "최예원", "loopy.wrkr", "INFRA", "팀원", "010-1161-2222", Role.USER, "profile_f.jpg"),

    ADMIN_A(29L, "admin1@gmail.com", "AdminPass01*", "관리자1", "admina.wrkr", "ADMIN", "팀장", "010-1234-5679", Role.ADMIN, "admin_profile_a.jpg"),
    ADMIN_B(30L, "admin2@gmail.com", "AdminPass02*", "관리자2", "adminb.wrkr", "ADMIN", "팀장", "010-9876-5480", Role.ADMIN, "admin_profile_b.jpg"),
    ADMIN_C(31L, "admin3@gmail.com", "AdminPass03*", "관리자3", "adminc.wrkr", "ADMIN", "팀장", "010-5678-1861", Role.ADMIN, "admin_profile_c.jpg"),
    ADMIN_D(32L, "admin4@gmail.com", "AdminPass04*", "관리자4", "admind.wrkr", "ADMIN", "팀장", "010-2468-1392", Role.ADMIN, "admin_profile_d.jpg"),
    ADMIN_E(33L, "admin5@gmail.com", "AdminPass05*", "관리자5", "admine.wrkr", "ADMIN", "팀장", "010-1131-2203", Role.ADMIN, "admin_profile_e.jpg"),
    ADMIN_F(34L, "admin6@gmail.com", "AdminPass06*", "관리자6", "adminh.wrkr", "ADMIN", "팀장", "010-1811-2224", Role.ADMIN, "admin_profile_f.jpg"),
    ADMIN_G(35L, "admin7@gmail.com", "AdminPass07*", "관리자7", "adminj.wrkr", "ADMIN", "팀장", "010-1111-2533", Role.ADMIN, "admin_profile_g.jpg"),
    ADMIN_H(36L, "admin8@gmail.com", "AdminPass08*", "관리자8", "admini.wrkr", "ADMIN", "팀장", "010-7211-2234", Role.ADMIN, "admin_profile_h.jpg"),
    ADMIN_I(37L, "admin9@gmail.com", "AdminPass09*", "관리자9", "admink.wrkr", "ADMIN", "팀장", "010-1161-2345", Role.ADMIN, "admin_profile_i.jpg"),

    ;

    private final Long memberId;
    private final String email;
    private final String password;
    private final String name;
    private final String nickname;
    private final String department;
    private final String position;
    private final String phone;
    private final Role role;
    private final String profileImage;

    UserFixture(
        Long memberId,
        String email,
        String password,
        String name,
        String nickname,
        String department,
        String position,
        String phone,
        Role role,
        String profileImage
    ) {
        this.memberId = memberId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.department = department;
        this.position = position;
        this.phone = phone;
        this.role = role;
        this.profileImage = profileImage;
    }

    public Member toMember() {
        return Member.builder()
            .memberId(memberId)
            .email(email)
            .password(encodePassword(password))
            .name(name)
            .nickname(nickname)
            .department(department)
            .position(position)
            .phone(phone)
            .role(role)
            .profileImage(profileImage)
            .isTempPassword(false)
            .build();
    }

    public String getRawPassword() {
        return this.password;
    }
}
