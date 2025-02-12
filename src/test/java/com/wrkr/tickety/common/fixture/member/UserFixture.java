package com.wrkr.tickety.common.fixture.member;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.domain.model.Member;
import lombok.Getter;

@Getter
public enum UserFixture {

    USER_A(1L, "thama@naver.com", "Dummy0212*", "김민형", "thama.wrkr", "INFRA", "팀원", "01012345678", Role.USER, "profile_a.jpg"),
    USER_B(2L, "wonee@naver.com", "Dummy0212*", "김지원", "wonee.wrkr", "INFRA", "팀원", "01098765432", Role.USER, "profile_b.jpg"),
    USER_C(3L, "curi@naver.com", "Dummy0212*", "강수진", "curi.wrkr", "INFRA", "팀원", "01056781234", Role.USER, "profile_c.jpg"),
    USER_D(4L, "yosbi@naver.com", "Dummy0212*", "하재웅", "yosbi.wrkr", "INFRA", "팀원", "01024681357", Role.USER, "profile_d.jpg"),
    USER_E(5L, "tomcat@naver.com", "Dummy0212*", "박선웅", "tomcat.wrkr", "INFRA", "팀원", "01011312222", Role.USER, "profile_e.jpg"),
    USER_F(6L, "terry@naver.com", "Dummy0212*", "한상결", "terry.wrkr", "INFRA", "팀원", "01011112222", Role.USER, "profile_f.jpg"),
    USER_G(7L, "phurray@naver.com", "Dummy0212*", "김민서", "phurray.wrkr", "INFRA", "팀원", "010111124522", Role.USER, "profile_g.jpg"),
    USER_H(8L, "harrison@naver.com", "Dummy0212*", "박정재", "harrison.wrkr", "INFRA", "팀원", "01077112222", Role.USER, "profile_h.jpg"),
    USER_I(9L, "loopy@naver.com", "Dummy0212*", "최예원", "loopy.wrkr", "INFRA", "팀원", "01011612222", Role.USER, "profile_f.jpg"),

    MANAGER_A(11L, "thama@gmail.com", "Dummy0212*", "김민형", "thama.kakao", "BACKEND", "팀원", "01012345618", Role.MANAGER, "profile_a.jpg"),
    MANAGER_B(12L, "wonee@gmail.com", "Dummy0212*", "김지원", "wonee.kakao", "BACKEND", "팀원", "01098765932", Role.MANAGER, "profile_b.jpg"),
    MANAGER_C(13L, "curi@gmail.com", "Dummy0212*", "강수진", "curi.kakao", "BACKEND", "팀원", "01056781664", Role.MANAGER, "profile_c.jpg"),
    MANAGER_D(14L, "yosbi@gmail.com", "Dummy0212*", "하재웅", "yosbi.kakao", "BACKEND", "팀원", "01024681390", Role.MANAGER, "profile_d.jpg"),
    MANAGER_E(15L, "tomcat@gmail.com", "Dummy0212*", "박선웅", "tomcat.kakao", "BACKEND", "팀원", "01011312202", Role.MANAGER, "profile_e.jpg"),
    MANAGER_F(16L, "terry@gmail.com", "Dummy0212*", "한상결", "terry.kakao", "BACKEND", "팀원", "01018112222", Role.MANAGER, "profile_f.jpg"),
    MANAGER_G(17L, "phurray@gmail.com", "Dummy0212*", "김민서", "phurray.kakao", "BACKEND", "팀원", "010111123522", Role.MANAGER, "profile_g.jpg"),
    MANAGER_H(18L, "harrison@gmail.com", "Dummy0212*", "박정재", "harrison.kakao", "BACKEND", "팀원", "01072112222", Role.MANAGER, "profile_h.jpg"),
    MANAGER_I(19L, "loopy@gmail.com", "Dummy0212*", "최예원", "loopy.kakao", "BACKEND", "팀원", "01011612232", Role.MANAGER, "profile_f.jpg"),
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
            .password(password)
            .name(name)
            .nickname(nickname)
            .department(department)
            .position(position)
            .phone(phone)
            .role(role)
            .profileImage(profileImage)
            .build();
    }
}
